package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Subsistema do INTAKE.
 *
 * Dois motores que giram sempre juntos para coletar ou expelir peças.
 */
public class Intake {

    // ===================== CONSTANTES DE AJUSTE =====================

    /** Potência usada para coletar peças. */
    public static double POTENCIA_COLETAR = 0.7;

    /** Potência usada para expelir peças (negativa = sentido contrário). */
    public static double POTENCIA_EXPELIR = -0.9;

    /** Potência com o intake parado. */
    public static double POTENCIA_PARADO = 0.0;

    // ===================== ESTADO =====================

    /** Possíveis estados do intake (usado na telemetria). */
    public enum Estado { COLETANDO, EXPELINDO, PARADO }

    private Estado estadoAtual = Estado.PARADO;

    // ===================== HARDWARE =====================

    private final DcMotor motorIntake1;
    private final DcMotor motorIntake2;

    /**
     * Construtor: busca os motores do intake na configuração do robô e define o sentido de rotação.
     *
     * @param hardwareMap mapa de hardware recebido do OpMode
     */
    public Intake(HardwareMap hardwareMap) {
        // Os textos entre aspas são os nomes cadastrados na configuração do Driver Station
        motorIntake1 = hardwareMap.get(DcMotor.class, "intake");
        motorIntake2 = hardwareMap.get(DcMotor.class, "intake2");

        motorIntake1.setDirection(DcMotor.Direction.FORWARD);
        motorIntake2.setDirection(DcMotor.Direction.FORWARD);
    }

    /** Liga o intake no sentido de coletar peças. */
    public void coletar() {
        definirPotencia(POTENCIA_COLETAR);
        estadoAtual = Estado.COLETANDO;
    }

    /** Liga o intake no sentido de expelir peças. */
    public void expelir() {
        definirPotencia(POTENCIA_EXPELIR);
        estadoAtual = Estado.EXPELINDO;
    }

    /** Desliga o intake. */
    public void parar() {
        definirPotencia(POTENCIA_PARADO);
        estadoAtual = Estado.PARADO;
    }

    /**
     * Aplica a mesma potência nos dois motores do intake.
     *
     * @param potencia valor de -1 a 1
     */
    public void definirPotencia(double potencia) {
        motorIntake1.setPower(potencia);
        motorIntake2.setPower(potencia);
    }

    /** @return o estado atual do intake (COLETANDO, EXPELINDO ou PARADO) */
    public Estado getEstadoAtual() {
        return estadoAtual;
    }
}
