package frc.robot.subsystems.LED;

import edu.wpi.first.wpilibj.PWM;

public class LEDIOReal implements LEDIO {
    private final PWM led = new PWM(0);
    private int setSpeed = -99;

    @Override
    public void setPWMState(int signal) {
        led.setPulseTimeMicroseconds(signal);
        setSpeed = signal;
    }

    @Override
    public void updateInputs(LEDIOInputs inputs) {
        inputs.pwmSpeed = led.getSpeed();
        inputs.pwmSetSpeed = setSpeed;
    }
}
