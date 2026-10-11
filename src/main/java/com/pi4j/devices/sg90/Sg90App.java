/*
 *  /*
 *  *
 *
 */

package com.pi4j.devices.sg90;

import com.pi4j.Pi4J;
import com.pi4j.boardinfo.util.PwmChipUtil;
import com.pi4j.context.Context;
import com.pi4j.drivers.motor.sg90.Sg90Driver;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.util.Console;
import com.pi4j.util.Delay;

/**
 * SG90 servo motor control.  The driver will use the PWM device created by
 * this app. It is assumed the device is a HardWare PWM.  Hardware PWM creates
 * a more consistent PWM signal.
 * <p>
 * See README.md for more information and detail.
 */
class Sg90App {

    private static final int DEFAULT_PWM_FREQUENCY = 50;
    private static final int DEFAULT_CHANNEL_NUMBER = 2;
    private static final int SERVO_NUMBER = 1;

    private static final double LOW_PULSSE = 1000.0;
    private static final double HIGH_PULSE = 2000.0;

    private static Integer channel = DEFAULT_CHANNEL_NUMBER;
    private static Console console;
    private static Sg90Driver sg90;
    private static final Delay delay = new Delay();
    private static java.util.Scanner scanner;
    private static double lowPulse = LOW_PULSSE;
    private static double highPulse = HIGH_PULSE;


    public Sg90App() {
        super();

    }


    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    static void main(String[] args) throws Exception {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

        scanner = new java.util.Scanner(System.in);

        console = new Console();
        Context pi4j = Pi4J.newAutoContext();
        console.title("<-- The Pi4J V5 Project Extension  -->", "Sg90App");
        String helpString = " Parms: -c channel   -q quit -low microseconds -high microseconds -h HELP";


        String o;
        for (int i = 0; i < args.length; i++) {
            switch (o = args[i]) {
                case "-low" -> {
                    lowPulse = Integer.parseInt(args[i + 1]);
                    i++;
                }
                case "-high" -> {
                    highPulse = Integer.parseInt(args[i + 1]);
                    i++;
                }
                case "-c" -> {
                    channel = Integer.parseInt(args[i + 1]);
                    i++;
                }
                case "-q" -> {
                    console.println("Exit");
                    System.exit(40);
                }
                case "-h" -> {
                    console.println(helpString);
                    System.exit(41);
                }
                default -> {
                    console.println("  !!! Invalid Parm " + o);
                    console.println(helpString);
                    System.exit(42);
                }
            }
        }


        Pwm pwm = createPwm(SERVO_NUMBER, channel, pi4j);

        sg90 = new Sg90Driver(pwm, lowPulse, highPulse);

        waitChange(10L);

        pwm.close();

    }

    static void waitChange(long c) {
        while (true) {
            delay.setMillis(c).materialize();
            console.println("Enter degree value or enter any key to quit");
            if (scanner.hasNextInt()) {
                double nextDegree = scanner.nextDouble();
                if ((nextDegree < 0.0) || (nextDegree > 180.0)) {
                    console.println("  degree must be in the range 0..180");
                } else {
                    sg90.setServoAngle(nextDegree);
                }
            } else {
                break;
            }
        }
    }


    static Pwm createPwm(int servoNumber, Integer channel, Context pi4j) {

        final PwmConfig config = PwmConfigBuilder.newInstance(pi4j)
            .id("sg90Number" + servoNumber)
            .name("SG90 number " + servoNumber)
            .channel(channel)
            .pwmType(PwmType.HARDWARE)
            .channel(channel)
            .chip(PwmChipUtil.getPWMChip())
            .frequency(DEFAULT_PWM_FREQUENCY)
            .build();

        return pi4j.create(config);
    }

}
