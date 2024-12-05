package frc.robot.subsystems.manager;

import frc.robot.pioneersLib.subsystem.SubsystemStates;
import frc.robot.subsystems.LED.LEDStates;

public enum ManagerStates implements SubsystemStates {
    RED("Red", LEDStates.RED),
    GREEN("Green", LEDStates.GREEN),
    BLUE("Blue", LEDStates.BLUE);

    private LEDStates LEDState;
    private String stateString;
        
    ManagerStates(String stateString, LEDStates LEDState) {
        this.LEDState = LEDState;
        this.stateString = stateString;
    }

    @Override
    public String getStateString() {
        return stateString;
    }

    public LEDStates getLEDState() {
        return LEDState;
    }
}
