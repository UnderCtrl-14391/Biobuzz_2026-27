package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous
public class autonomoIgao2 extends AutonomoIgao{

    @Override
    public void autonomous(int Dfrente, int Efrente, int trasD, int trasE, double Dpower, double Epower, double intake, double shooter1, double shooter2) {
        super.autonomous(Dfrente, Efrente, trasD, trasE, Dpower, Epower, intake, shooter1, shooter2);

        autonomous(1000,1000,1000,1000,0.5,0.5,0,0,0);
    }
}
