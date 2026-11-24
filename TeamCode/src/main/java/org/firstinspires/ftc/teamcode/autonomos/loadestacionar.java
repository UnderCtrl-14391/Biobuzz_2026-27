package org.firstinspires.ftc.teamcode.autonomos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Liberador;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

@Autonomous(name = "loadEstacionar", group = "Autonomous")
public class loadestacionar  extends AutoFunction {
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


            shooter.ligarPolenAuto();
            while (opModeIsActive() && !shooter.estaProntoParaAtirarAuto()) {
            }
            sleep(2000);
            liberador.liberarTrava2();
            setFlowers(true);
            intake.coletarAutonomo();
            sleep(4000);
            setFlowers(false);

            stopResetMotors();
            autonomous(-900,-900,-900,-900,0.6,0.6,0,0,0,1,0,0,0, 600);

            stopResetMotors();
            autonomous(3000,-3000,3000,-3000,0.6,0.6,0,0,0,1,0,0,0,3000);


        }
    }
}

