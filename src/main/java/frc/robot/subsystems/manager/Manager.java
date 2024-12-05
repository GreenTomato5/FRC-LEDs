package frc.robot.subsystems.manager;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.LED.LED;

public class Manager extends Subsystem<ManagerStates> {

    private static Manager instance;

    private LED led = LED.getInstance();

    private Manager() {
        super("Manager", ManagerStates.RED);

        addTrigger(ManagerStates.RED, ManagerStates.GREEN, Constants.Controllers.driverController::getAButtonPressed);
        addTrigger(ManagerStates.GREEN, ManagerStates.RED, Constants.Controllers.driverController::getAButtonPressed);
        addTrigger(ManagerStates.RED, ManagerStates.BLUE, Constants.Controllers.driverController::getBButtonPressed);
        addTrigger(ManagerStates.BLUE, ManagerStates.RED, Constants.Controllers.driverController::getBButtonPressed);
    }

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    @Override
    public void runState() {
        led.setState(getState().getLEDState());
        led.runState();

        SmartDashboard.putString("Manager State", getState().getStateString());
    }
}
