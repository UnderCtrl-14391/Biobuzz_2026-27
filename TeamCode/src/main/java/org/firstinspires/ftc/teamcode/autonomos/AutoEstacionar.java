package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous (name = "AutoEstacionar", group = "Autonomous")
public class AutoEstacionar extends AutoFunction {

    @Override
    public void runOpMode(){
        HardwareMap(hardwareMap);
        waitForStart();

        //estacionar
        stopResetMotors();
        autonomous(-2000,2000,-2000,2000,0.5,0.5,0,0,0,0,0,0,3000,900);
    }
}
