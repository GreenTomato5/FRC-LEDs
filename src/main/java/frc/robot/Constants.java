package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

public class Constants {
    public enum RobotMode {
        REAL, SIM
    }

    public static final RobotMode ROBOT_MODE = RobotMode.REAL;


    public static final class Controllers {
        public static final XboxController driverController = new XboxController(0);
    }

    public static final class LED {
        public static final int LED_PWM_PORT = 0;

    }
}
