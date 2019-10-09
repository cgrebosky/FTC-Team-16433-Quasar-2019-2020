package quasar.lib;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.jetbrains.annotations.NotNull;

/**
 * This is used to get the state of a gamepad at a single moment of time.  Yes, this was a bitch to
 * program.
 */
public class GamepadState {
    //All variables are copied from Gamepad.java, the class we commonly use in opmodes
    public float left_stick_x = 0f;
    public float left_stick_y = 0f;
    public float right_stick_x = 0f;
    public float right_stick_y = 0f;
    public boolean left_stick_button = false;
    public boolean right_stick_button = false;

    public boolean dpad_up = false;
    public boolean dpad_down = false;
    public boolean dpad_left = false;
    public boolean dpad_right = false;

    public boolean a = false;
    public boolean b = false;
    public boolean x = false;
    public boolean y = false;

    public boolean guide = false;
    public boolean start = false;
    public boolean back = false;

    public boolean left_bumper = false;
    public boolean right_bumper = false;
    public float left_trigger = 0f;
    public float right_trigger = 0f;

    public GamepadState(@NotNull Gamepad gamepad) {
        left_stick_x       = gamepad.left_stick_x;
        left_stick_y       = gamepad.left_stick_y;
        right_stick_x      = gamepad.right_stick_x;
        right_stick_y      = gamepad.right_stick_y;
        left_stick_button  = gamepad.left_stick_button;
        right_stick_button = gamepad.right_stick_button;

        dpad_up = gamepad.dpad_up;
        dpad_down = gamepad.dpad_down;
        dpad_left = gamepad.dpad_left;
        dpad_right = gamepad.dpad_right;

        a = gamepad.a;
        b = gamepad.b;
        x = gamepad.x;
        y = gamepad.y;

        guide = gamepad.guide;
        start = gamepad.start;
        back = gamepad.back;

        left_bumper = gamepad.left_bumper;
        right_bumper = gamepad.right_bumper;
        left_trigger = gamepad.left_trigger;
        right_trigger = gamepad.right_trigger;

    }
    public GamepadState() {}
}
