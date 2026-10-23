package com.pi4j.examples.utils.commandhandler;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility that is able to configure a device from command line parameters and can perform operations on the device.
 * <p>
 * Generally, the device will be (re)constructed when any action is triggered and the settings have changed.
 * <p>
 * @param <T> The device type handled by this class.
 */
public class DeviceCommandHandler<T> {
    private final Map<String, Object> settings = new TreeMap<>();
    private final Map<String, Command<?>> commands = new LinkedHashMap<>();
    private final Function<Map<String, Object>, T> deviceFactory;
    private T device;
    private Command<?> currentCommand;
    private String defaultAction;
    private boolean consumeStdin;
    private boolean exit;

    public DeviceCommandHandler(Function<Map<String, Object>, T> deviceFactory) {
        this.deviceFactory = deviceFactory;
        utility("h", "Display this help text", () -> printHelp(System.out));
        utility("-", "Read commands from stdin",  () -> consumeStdin = true);
        utility("x", "Exit", () -> exit = true);
    }

    private <P> DeviceCommandHandler<T> register(CommandType type, String label, String description, ParameterSpecification<P> paramSpec, BiConsumer<T, P> action) {
        label = label.toLowerCase(Locale.ROOT);
        if (commands.containsKey(label)) {
            throw new IllegalStateException("Label '" + label + "' already registered.");
        }
        if (type == CommandType.DEFAULT) {
            if (defaultAction != null) {
                throw new IllegalStateException("Only one default action supported.");
            }
            defaultAction = label;
        }
        commands.put(label, new Command<>(type, label, description, paramSpec, action));
        return this;
    }

    /** Adds a utility action that is not related to the device */
    public <P> DeviceCommandHandler<T> utility(String label, String description, Runnable action) {
        return register(CommandType.UTILITY, label, description, null, (_, pt) -> action.run());
    }


    /** Add a parameterized device action command */
    public <P> DeviceCommandHandler<T> parameterizedAction(String label, String description, ParameterSpecification<P> paramSpec, BiConsumer<T, P> action) {
        return register(CommandType.ACTION, label, description, paramSpec, action);
    }

    /** Add a parameterized utility action */
    public <P> DeviceCommandHandler<T> parameterizedUtility(String label, String description, ParameterSpecification<P> paramSpec, Consumer<P> action) {
        return register(CommandType.UTILITY, label, description, paramSpec, (_, pt) -> action.accept(pt));
    }

    /** Add a parameterless device action command. */
    public DeviceCommandHandler<T> action(String label, String description, Consumer<T> action) {
        return register(CommandType.ACTION, label, description, null, (device, _) -> action.accept(device));
    }

    /** Set the action that will be executed when no action is specified */
    public DeviceCommandHandler<T> setDefault(String label) {
        this.defaultAction = label;
        return this;
    }

    /** Add a setting used for constructing the device. */
    public <P> DeviceCommandHandler<T> configuration(String label, String description, ParameterSpecification<P> paramSpec, P defaultValue) {
        settings.put(label, defaultValue);
        return register(CommandType.SETTING, label, description, paramSpec, (_, value) -> {
            Object oldValue = settings.get(label);
            if (!value.equals(oldValue)) {
                settings.put(label, value);
                if (device != null) {
                    try {
                        if (device instanceof Closeable) {
                            ((Closeable) device).close();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        device = null;
                    }
                }
            }
        });
    }

    void printHelp(PrintStream out) {
        out.println("Available commands:");
        out.println();
        for (Map.Entry<String, Command<?>> entry: commands.entrySet()) {
            Command<?> command = entry.getValue();
            out.println(command.toString());
        }
        out.println();
    }

    public void process(String[] args) {
        for (String arg : args) {
            processImpl(arg);
        }
        if (defaultAction != null) {
            processImpl(defaultAction);
        }
        if (consumeStdin) {
            Scanner scanner = new Scanner(System.in);
            while (!exit && scanner.hasNext()) {
                processImpl(scanner.next());
            }
        }
    }

    void processImpl(String s) {
        s = s.trim();
        if (currentCommand == null) {
            if (s.startsWith("-") && s.length() > 1) {
                s = s.substring(1);
            }
            currentCommand = commands.get(s.toLowerCase(Locale.ROOT));
            if (currentCommand == null) {
                System.err.println("Command '" + s + "' not found.");
                printHelp(System.err);
                return;
            }
            if (currentCommand.parameterSpecification != null) {
                return;
            }
        }
        try {
            currentCommand.process(s);
        } catch (RuntimeException e) {
            System.err.println("Error processing command '" + currentCommand.label + "': " + e.getMessage());
            printHelp(System.err);
        }
        currentCommand = null;
    }


    class Command<P> {
        private final CommandType type;
        private final String label;
        private final String description;
        private final ParameterSpecification<P> parameterSpecification;
        private final BiConsumer<T, P> action;

        Command(CommandType type, String label, String description, ParameterSpecification<P> parameterSpecification, BiConsumer<T, P> action) {
            this.type = type;
            this.label = label;
            this.description = description;
            this.parameterSpecification = parameterSpecification;
            this.action = action;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(label);
            if (parameterSpecification != null) {
                sb.append(" ").append(parameterSpecification);
                if (type == CommandType.SETTING) {
                    sb.append(" (");
                    Object value = settings.get(label);
                    sb.append(value);
                    if (value instanceof Integer) {
                        sb.append(" / 0x").append(Integer.toHexString((Integer) value));
                    }
                    sb.append(')');
                }
            }
            sb.append(": ").append(description);
            return sb.toString();
        }

        void process(String s) {
            P parameter = parameterSpecification == null ? null : parameterSpecification.process(s);
            if ((type == CommandType.ACTION || type == CommandType.DEFAULT) && device == null) {
                device = deviceFactory.apply(settings);
            }
            if (type != CommandType.SETTING) {
                defaultAction = null;
            }
            action.accept(device, parameter);
        }
    }

    enum CommandType {
        SETTING, ACTION, DEFAULT, UTILITY
    }


}
