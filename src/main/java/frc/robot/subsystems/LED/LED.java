package frc.robot.subsystems.LED;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.Subsystem;

public class LED extends Subsystem<LEDStates> {

    private static LED instance;
    private LEDIO io;
    // private LEDIOInputsAutoLogged inputs = new LEDIOInputsAutoLogged();

    private LED() {
        super("LEDs", LEDStates.RED);
        io = switch (Constants.ROBOT_MODE) {
            case SIM -> new LEDIOSim();
            case REAL -> new LEDIOReal();
            default -> new LEDIO() {
            };
        };
    }

    public static LED getInstance() {
        if (instance == null) {
            instance = new LED();
        }
        return instance;
    }

    @Override
    public void runState() {
        io.setPWMState(getState().getSignal());
        // TODO: I lowkey broke akit install so this don't work
        // Logger.processInputs("LED", inputs);
        SmartDashboard.putString("LED State", getState().getStateString());
    }

}
