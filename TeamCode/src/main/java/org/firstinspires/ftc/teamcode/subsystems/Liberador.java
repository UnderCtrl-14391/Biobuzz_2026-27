package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Subsistema do LIBERADOR.
 *
 * Servo que fica SEGURANDO o pólen antes do shooter e só o LIBERA
 * quando o shooter está rápido o suficiente para atirar.
 * Ele tem duas posições: SEGURANDO (bloqueia a passagem) e LIBERANDO (abre a passagem).
 * Se o servo não estiver na configuração do robô, o código continua funcionando
 * (apenas ignora os comandos) e o teleop mostra um aviso na telemetria.
 */
public class Liberador {

    // ===================== CONSTANTES DE AJUSTE =====================

    /** Posição do servo segurando o pólen, com a passagem fechada (0.0 a 1.0). */
    public static double POSICAO_SEGURAR = 0;

    /** Posição do servo liberando o pólen para o shooter, com a passagem aberta (0.0 a 1.0). */
    public static double POSICAO_LIBERAR = 1;

    // ===================== ESTADO =====================

    /** Possíveis estados do liberador. */
    public enum Estado { SEGURANDO, LIBERANDO }

    private Estado estadoAtual = Estado.SEGURANDO;

    // ===================== HARDWARE =====================

    private Servo servoLiberador;

    /**
     * Construtor: busca o servo do liberador e já o deixa segurando o pólen.
     *
     * @param hardwareMap mapa de hardware recebido do OpMode
     */
    public Liberador(HardwareMap hardwareMap) {
        try {
            // O texto entre aspas é o nome cadastrado na configuração do Driver Station
            servoLiberador = hardwareMap.get(Servo.class, "trava2");
        } catch (Exception erro) {
            servoLiberador = null;
        }
        segurar();
    }

    /** Fecha a passagem: o servo segura o pólen e impede que ele chegue ao shooter. */
    public void segurar() {
        estadoAtual = Estado.SEGURANDO;
        if (servoLiberador != null) servoLiberador.setPosition(POSICAO_SEGURAR);
    }

    /** Abre a passagem: o servo solta o pólen para ele seguir até o shooter. */
    public void liberar() {
        estadoAtual = Estado.LIBERANDO;
        if (servoLiberador != null) servoLiberador.setPosition(POSICAO_LIBERAR);
    }

    /** @return o estado atual do liberador (SEGURANDO ou LIBERANDO) */
    public Estado getEstadoAtual() {
        return estadoAtual;
    }

    /** @return true se o servo foi encontrado na configuração do robô */
    public boolean isConectado() {
        return servoLiberador != null;
    }
}
