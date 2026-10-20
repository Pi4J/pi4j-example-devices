package com.pi4j.devices.commandhandler;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility that is able to configure a device from command line parameters and can perform operations on the device.
 */
public class DeviceCommandHandler<DT> {
    private final Map<String, Object> settings = new TreeMap<>();
    private final Map<String, Command<?>> commands = new LinkedHashMap<>();
    private final Function<Map<String, Object>, DT> deviceFactory;
    private DT device;
    private Command<?> currentCommand;
    private String defaultAction;

    public DeviceCommandHandler(Function<Map<String, Object>, DT> deviceFactory) {
        this.deviceFactory = deviceFactory;
        utility("h", "Display this help text", null, (_) -> printHelp(System.out));
    }

    private <PT> DeviceCommandHandler<DT> register(CommandType type, String label, String description, ParameterSpecification<PT> paramSpec, BiConsumer<DT, PT> action) {
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

    public <PT> DeviceCommandHandler<DT> utility(String label, String description, ParameterSpecification<PT> paramSpec, Consumer<PT> action) {
        return register(CommandType.UTILITY, label, description, paramSpec, (_, pt) -> action.accept(pt));
    }

    public <PT> DeviceCommandHandler<DT> parameterizedAction(String label, String description, ParameterSpecification<PT> paramSpec, BiConsumer<DT, PT> action) {
        return register(CommandType.ACTION, label, description, paramSpec, action);
    }

    public DeviceCommandHandler<DT> action(String label, String description, Consumer<DT> action) {
        return register(CommandType.ACTION, label, description, null, (device, _) -> action.accept(device));
    }


    public DeviceCommandHandler<DT> defaultAction(String label, String description, Consumer<DT> action) {
        return register(CommandType.DEFAULT, label, description, null, (device, _) -> action.accept(device));
    }

    public <PT> DeviceCommandHandler<DT> setting(String label, String description, ParameterSpecification<PT> paramSpec, PT defaultValue) {
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
        out.println("Available parameters:");
        out.println();
        for (Map.Entry<String, Command<?>> entry: commands.entrySet()) {
            Command<?> command = entry.getValue();
            out.println(command.toString());
        }
        out.println();
    }

    public void process(String[] args) {
        try {
            for (String arg : args) {
                processImpl(arg);
            }
            if (defaultAction != null) {
                processImpl(defaultAction);
            }
        } catch (RuntimeException e) {
            System.err.println("Command processing error: " + e.getMessage());
            printHelp(System.err);
        }
    }

    void processImpl(String s) {
        s = s.trim();
        if (currentCommand == null) {
            if (s.startsWith("-")) {
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
        currentCommand.process(s);
        currentCommand = null;
    }


    class Command<PT> {
        private final CommandType type;
        private final String label;
        private final String description;
        private final ParameterSpecification<PT> parameterSpecification;
        private final BiConsumer<DT, PT> action;

        Command(CommandType type, String label, String description, ParameterSpecification<PT> parameterSpecification, BiConsumer<DT, PT> action) {
            this.type = type;
            this.label = label;
            this.description = description;
            this.parameterSpecification = parameterSpecification;
            this.action = action;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("-");
            sb.append(label);
            if (parameterSpecification != null) {
                sb.append(" ").append(parameterSpecification);
                if (type == CommandType.SETTING) {
                    sb.append(" (").append(settings.get(label)).append(')');
                }
            }
            sb.append(": ").append(description);
            return sb.toString();
        }

        void process(String s) {
            PT parameter = parameterSpecification == null ? null : parameterSpecification.process(s);
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


    public static abstract class ParameterSpecification<PT> {
        abstract PT process(String value);
    }


    public static class IntegerParameter extends ParameterSpecification<Integer> {
        private final int min;
        private final int max;

        public IntegerParameter(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        Integer process(String text) {
            int parsed = Integer.parseInt(text);
            if (parsed < min || parsed > max) {
                throw new IllegalArgumentException("Value " + text);
            }
            return parsed;
        }

        public String toString() {
            return "[" + min + ".." + max + "]";
        }
    }
}
