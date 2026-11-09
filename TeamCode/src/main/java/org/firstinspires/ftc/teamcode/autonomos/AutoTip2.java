package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Liberador;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

@Autonomous (name = "AutoTip2", group = "Autonomous")
public class AutoTip2 extends AutoFunction {
    private Intake intake;
    private Shooter shooter;
    private Liberador liberador;

    @Override
    public void runOpMode() {
        HardwareMap(hardwareMap);
        waitForStart();
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        liberador = new Liberador(hardwareMap);

        if (opModeIsActive()) {

            stopResetMotors();
            autonomous(0,0,0,0,0,0,0,0,0,0.6,0,0,0, 100);

            shooter.ligar();
            while (!shooter.estaProntoParaAtirar()) {
            }
            sleep(1000);
            liberador.liberarTrava2();
            setFlowers(true);
            intake.coletarAutonomo();
            sleep(4000);
            setFlowers(false);

            stopResetMotors();
            autonomous(-600,-600,-600,-600,0.6,0.6,0,0,0,0.6,0,0,2000, 600);

            stopResetMotors();
            autonomous(600,-600,600,-600,0.6,0.6,0,0,0,0.6,0,0,1000,700);

            stopResetMotors();
            autonomous(400,400,400,400,0.2,0.2,0.8,0,0,0.3,0,0,1000, 7000);

            stopResetMotors();
            autonomous(0,0,0,0,0,0,0.6,0,0,0.6,0.2,0,0,700);

            stopResetMotors();
            autonomous(-600,-600,-600,-600,0.6,0.6,0,0,0,0.6,0,0,2000, 600);

            stopResetMotors();
            autonomous(-600,600,-600,600,0.3,0.3,0,0,0,0.6,0,0,0,700);

            stopResetMotors();
            autonomous(600,600,600,600,0.2,0.2,0.8,0,0,0.6,0,0,1000, 7000);



            shooter.ligar();
            while (!shooter.estaProntoParaAtirar()) {
            }
            sleep(1000);
            liberador.liberarTrava2();
            setFlowers(true);
            intake.coletarAutonomo();
            sleep(4000);
            setFlowers(false);

            stopResetMotors();
            autonomous(1000,-1000,1000,-1000,0.6,0.6,0,0,0,0,0,0,2000,700);

            //stopResetMotors();
            //autonomous(-1000,-1000,-1000,-1000,0.6,0.6,0,0,0,0,0,0,2000,700);
        }
    }
}
