package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Subsistema dos INDEXERS (flower1 e flower2).
 *
 * Dois servos de rotação contínua (CRServo) ou posicionais (Servo) que giram/acionam
 * para levar o pólen até o liberador.
 */
public class Indexer {

    // ===================== CONSTANTES DE AJUSTE =====================

    /** Potência dos indexers ao alimentar (-1 a 1). */
    public static double POTENCIA_ALIMENTAR = 1.0;

    /** Potência baixa usada para enfileirar (-1 a 1). */
    public static double POTENCIA_ENFILEIRAR = 0.3;

    // ===================== ESTADO =====================

    private boolean girando = false;

    // ===================== HARDWARE =====================

    private CRServo flower1;
    private CRServo flower2;
    private Servo flower1Posicional;
    private Servo flower2Posicional;

    /** Fica false se nenhum servo foi encontrado na configuração. */
    private boolean conectado = true;

    /**
     * Construtor: busca os servos flower1 e flower2 e garante que comecem parados.
     *
     * @param hardwareMap mapa de hardware recebido do OpMode
     */
    public Indexer(HardwareMap hardwareMap) {
        try {
            flower1 = hardwareMap.get(CRServo.class, "flower1");
        } catch (Exception e1) {
            try {
                flower1 = hardwareMap.get(CRServo.class, "flower");
            } catch (Exception e2) {
                try {
                    flower1Posicional = hardwareMap.get(Servo.class, "flower1");
                } catch (Exception e3) {
                    try {
                        flower1Posicional = hardwareMap.get(Servo.class, "flower");
                    } catch (Exception ignored) {}
                }
            }
        }

        try {
            flower2 = hardwareMap.get(CRServo.class, "flower2");
        } catch (Exception e1) {
            try {
                flower2Posicional = hardwareMap.get(Servo.class, "flower2");
            } catch (Exception ignored) {}
        }

        if (flower1 != null) flower1.setDirection(DcMotorSimple.Direction.FORWARD);
        if (flower2 != null) flower2.setDirection(DcMotorSimple.Direction.FORWARD);

        if (flower1 == null && flower2 == null && flower1Posicional == null && flower2Posicional == null) {
            conectado = false;
        }

        parar();
    }

    /** Gira/aciona os indexers flower1 e flower2. */
    public void alimentar() {
        definirPotenciaOuPosicao(POTENCIA_ALIMENTAR, 1.0);
        girando = true;
    }

    /** Gira os indexers devagar. */
    public void enfileirar() {
        definirPotenciaOuPosicao(POTENCIA_ENFILEIRAR, 0.5);
        girando = true;
    }

    /** Para os indexers flower1 e flower2. */
    public void parar() {
        definirPotenciaOuPosicao(0.0, 0.0);
        girando = false;
    }

    /**
     * Aplica a potência ou posição aos servos flower1 e flower2.
     */
    private void definirPotenciaOuPosicao(double potencia, double posicao) {
        if (flower1 != null) flower1.setPower(potencia);
        if (flower2 != null) flower2.setPower(potencia);
        if (flower1Posicional != null) flower1Posicional.setPosition(posicao);
        if (flower2Posicional != null) flower2Posicional.setPosition(posicao);
    }

    /** @return true se os indexers estão girando */
    public boolean estaGirando() {
        return girando;
    }

    /** @return true se algum servo foi encontrado na configuração */
    public boolean isConectado() {
        return conectado;
    }
}
