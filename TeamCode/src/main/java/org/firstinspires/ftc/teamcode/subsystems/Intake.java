package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Subsistema do INTAKE.
 *
 * Um motor que gira para coletar ou expelir peças.
 */
public class Intake {

    // ===================== CONSTANTES DE AJUSTE =====================

    /** Potência usada para coletar peças via botão digital. */
    public static double POTENCIA_COLETAR = -0.9;

    /** Potência usada para expelir peças via botão digital. */
    public static double POTENCIA_EXPELIR = 0.9;

    /** Potência com o intake parado. */
    public static double POTENCIA_PARADO = 0.0;

    // ===================== ESTADO =====================

    /** Possíveis estados do intake (usado na telemetria). */
    public enum Estado { COLETANDO, EXPELINDO, PARADO }

    private Estado estadoAtual = Estado.PARADO;

    // ===================== HARDWARE =====================

    private final DcMotor motorIntake;

    /**
     * Construtor: busca o motor do intake na configuração do robô e define o sentido de rotação.
     *
     * @param hardwareMap mapa de hardware recebido do OpMode
     */
    public Intake(HardwareMap hardwareMap) {
        // O texto entre aspas é o nome cadastrado na configuração do Driver Station
        motorIntake = hardwareMap.get(DcMotor.class, "intake");
        motorIntake.setDirection(DcMotor.Direction.FORWARD);
    }

    /** Liga o intake no sentido de coletar peças (potência fixa). */
    public void coletar() {
        definirPotencia(POTENCIA_COLETAR);
        estadoAtual = Estado.COLETANDO;
    }

    /** Liga o intake no sentido de expelir peças (potência fixa). */
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
     * Controla o intake de forma proporcional via analógico do gamepad.
     *
     * No FTC Gamepad:
     *   - Empurrar para FRENTE gera valores NEGATIVOS -> COLETA (potência negativa)
     *   - Puxar para TRÁS gera valores POSITIVOS -> EXPELI (potência positiva)
     *
     * @param valorAnalogico valor do eixo do analógico (-1.0 a 1.0)
     */
    public void controlarPorAnalogico(double valorAnalogico) {
        if (Math.abs(valorAnalogico) < 0.1) {
            parar();
            return;
        }

        definirPotencia(valorAnalogico);

        if (valorAnalogico < 0) {
            estadoAtual = Estado.COLETANDO;
        } else {
            estadoAtual = Estado.EXPELINDO;
        }
    }

    /**
     * Aplica a potência no motor do intake.
     *
     * @param potencia valor de -1 a 1
     */
    public void definirPotencia(double potencia) {
        motorIntake.setPower(potencia);
    }

    /** @return o estado atual do intake (COLETANDO, EXPELINDO ou PARADO) */
    public Estado getEstadoAtual() {
        return estadoAtual;
    }
}
