package frc.robot.subsystems.LED;

import frc.robot.pioneersLib.subsystem.SubsystemStates;

public enum LEDStates implements SubsystemStates {
    RED("Red", 500),
    GREEN("Green", 1500),
    BLUE("Blue", 2500),
    WHITE("White", 3500);

    private int pwmSignal;
    private String stateString;

    LEDStates(String stateString, int pwmSignal) {
        this.pwmSignal = pwmSignal;
        this.stateString = stateString;
    }

    @Override
    public String getStateString() {
        return stateString;
    }
    
    public int getSignal() {
        return pwmSignal;
    }
}
