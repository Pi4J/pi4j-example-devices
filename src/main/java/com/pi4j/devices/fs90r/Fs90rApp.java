/*
 *  /*
 *  *
 *
 */

package com.pi4j.devices.fs90r;


import com.pi4j.drivers.motor.fs90r.Fs90RDriver;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.boardinfo.util.PwmChipUtil;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.util.Console;
import com.pi4j.util.Delay;

     /**
     * Example implementation for a FS90R servo motor.
     * The Fs90R defines a specific PWM interface and exactly how the PWN signal
     * controls the servo
     *
     * https://www.pololu.com/product/2820
     *
     *   See the README.md file for example usage and explanation.
     *
     *   FS90R servo motor control.  The driver will use the PWM device created by
     *   this app. It is assumed the device is a HardWare PWM.  Hardware PWM creates
     *   a more consistent PWM signal.
     */
    public class Fs90rApp {

        private static final int DEFAULT_PWM_FREQUENCY = 50;
        private static final int DEFAULT_CHANNEL_NUMBER = 2;
        private static final int SERVO_NUMBER = 1;


       private static double degree = 90.0;
        private static Integer channel = DEFAULT_CHANNEL_NUMBER ;
        private static Console console ;
        private static Fs90RDriver fs90R ;
        private static final Delay delay = new Delay();
        private static java .util.Scanner scanner;
        private static double lowPulse = 1000.0;
        private static double highPulse = 2000.0;


        public Fs90rApp() {
            super();

        }


        /**
         * <p>main.</p>
         * See the README.md file for example usage.
         * @param args an array of {@link java.lang.String} objects.
         * @throws java.lang.Exception if any.
         */
         static void main(String[] args) throws Exception {
           System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

           int onlyOne = 0;   // numerous parms a mutually exclusive

            scanner = new java.util.Scanner(System.in);

            console = new Console();
            Context pi4j = Pi4J.newAutoContext();
            console.title("<-- The Pi4J V5 Project Extension  -->", "Fs90rApp");
            String helpString = " Parms: -c channel -cw (clockwise) S M F  -ccw (counter-clockwise) S M F  -h HELP \n" +
                "    -low microseconds -high microseconds  -cw -ccw mutually exclusive  -d degree \n" +
                "    degree must be in the range 0..180. 90 stop, 0..89 CW, 91..180 CCW ";


            for (int i = 0; i < args.length; i++) {
                String o = args[i];
                if (o.contentEquals("-low")) {
                    String a = args[i + 1];
                    lowPulse = Double.parseDouble(a.substring(0));
                    i++;
                } else if (o.contentEquals("-high")) {
                    String a = args[i + 1];
                    highPulse = Double.parseDouble(a.substring(0));
                    i++;
                } else if (o.contentEquals("-d")) {
                    String a = args[i + 1];
                    degree = Double.parseDouble(a.substring(0));
                    if((!Double.isFinite(degree) || degree < 0) || (degree > 180)){
                        console.println("-d  degree must be in range 0..180");
                        System.exit(40);
                    }
                    onlyOne++;
                    i++;
                } else if (o.contentEquals("-c")) {
                    String a = args[i + 1];
                    channel = Integer.parseInt(a.substring(0));
                    i++;
                } else if (o.contentEquals("-cw")) {  /* 0-89 */
                    String a = args[i + 1];
                    onlyOne++;
                    i++;
                    if (a.equalsIgnoreCase("S")) {
                        degree = 75.0;
                    } else if (a.equalsIgnoreCase("M")) {
                        degree = 40.0;
                    }  else if (a.equalsIgnoreCase("F")) {
                        degree = 0.0;
                    } else {
                        console.println("  -cw invalid ");
                        System.exit(41);
                    }
                } else if (o.contentEquals("-ccw")) {  /* 191-180 */
                    String a = args[i + 1];
                    i++;
                    onlyOne++;
                   if (a.equalsIgnoreCase("S")) {
                        degree = 105.0;
                    } else if (a.equalsIgnoreCase("M")) {
                        degree = 140.0;
                    }  else if (a.equalsIgnoreCase("F")) {
                        degree = 180.0;
                    } else {
                        console.println("  -ccw invalid ");
                        System.exit(42);
                    }
                } else if (o.contentEquals("-q")) {
                    console.println("Exit");
                    System.exit(0);
                } else if (o.contentEquals("-h")) {
                    console.println(helpString);
                    System.exit(0);
                }else {
                    console.println("  !!! Invalid Parm " + o);
                    console.println(helpString);
                    System.exit(43);
                }
            }

            if (onlyOne > 1 ) {
                console.println(" mutually exclusive parms used.");
                console.println(helpString);
                System.exit(44);
            }
            Pwm pwm = createPwm(SERVO_NUMBER, channel, pi4j);

            fs90R = new Fs90RDriver(pwm, lowPulse, highPulse);

            // if user did not provide a -cw -ccw or -d   default sets stop
            fs90R.setServoRotation(degree);

            waitChange( 10l);

            pi4j.shutdown(pwm);

        }


        static Pwm createPwm(int servoNumber, Integer channel, Context pi4j) {

            final PwmConfig config = PwmConfigBuilder.newInstance (pi4j)
                .id ("fs90RNumber" + servoNumber)
                .name ("fs90R number " + servoNumber)
                .pwmType(PwmType.HARDWARE)
                .channel(channel)
                .chip(PwmChipUtil.getPWMChip())
                .frequency(DEFAULT_PWM_FREQUENCY)
                .build ();

            return pi4j.create (config);
        }

        static void waitChange(long c){
            while(true){
                delay.setMillis(c).materialize();
                console.println("Enter q - quit, or degree value ");
                if (scanner.hasNextInt() ) {
                    double nextDegree = scanner.nextDouble();
                    if((!Double.isFinite(nextDegree) || nextDegree < 0) || (nextDegree > 180)){
                        console.println(" degree must be in the range 0..180. 90 stop, 0..89 CW, 91..180 CCW");
                    }else {
                        fs90R.setServoRotation(nextDegree);
                    }
                } else{
                    break;
                }
            }
        }
    }





