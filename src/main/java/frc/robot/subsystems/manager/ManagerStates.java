package frc.robot.subsystems.manager;

import frc.robot.pioneersLib.subsystem.SubsystemStates;
import frc.robot.subsystems.LED.LEDStates;

public enum ManagerStates implements SubsystemStates {
    RED(LEDStates.RED),
    GREEN(LEDStates.GREEN),
    BLUE(LEDStates.BLUE);

    private LEDStates LEDState;
        
    ManagerStates(LEDStates LEDState) {
        this.LEDState = LEDState;
    }

    @Override
    public String getStateString() {
        return null;
    }

    public LEDStates getLEDState() {
        return LEDState;
    }
}
