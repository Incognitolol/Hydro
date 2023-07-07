package integral.studios.hydro.model.tracker.impl.entity;

import lombok.Getter;

@Getter
public class TrackedPosition {
    private int otherPlayerMPPosRotationIncrements;

    private double otherPlayerMPX, otherPlayerMPY, otherPlayerMPZ;

    private double posX, posY, posZ;

    private int serverPosX, serverPosY, serverPosZ;

    public TrackedPosition(double x, double y, double z) {
        serverPosX = doubleToInt(x);
        serverPosY = doubleToInt(y);
        serverPosZ = doubleToInt(z);

        posX = serverPosX / 32D;
        posY = serverPosY / 32D;
        posZ = serverPosZ / 32D;
    }


    public void onLivingUpdate() {
        if (otherPlayerMPPosRotationIncrements > 0) {
            double d0 = posX + (otherPlayerMPX - posX) / (double) otherPlayerMPPosRotationIncrements;
            double d1 = posY + (otherPlayerMPY - posY) / (double) otherPlayerMPPosRotationIncrements;
            double d2 = posZ + (otherPlayerMPZ - posZ) / (double) otherPlayerMPPosRotationIncrements;

            setPosition(d0, d1, d2);

            --otherPlayerMPPosRotationIncrements;
        }
    }

    public void handleMovement(double x, double y, double z) {
        serverPosX += doubleToInt(x);
        serverPosY += doubleToInt(y);
        serverPosZ += doubleToInt(z);

        setPositionAndRotation2(serverPosX / 32D, serverPosY / 32D, serverPosZ / 32D);
    }

    public void handleTeleport(double x, double y, double z) {
        serverPosX = doubleToInt(x);
        serverPosY = doubleToInt(y);
        serverPosZ = doubleToInt(z);

        double d0 = (double) serverPosX / 32.0D;
        double d1 = (double) serverPosY / 32.0D;
        double d2 = (double) serverPosZ / 32.0D;

        if (Math.abs(posX - d0) < 0.03125D && Math.abs(posY - d1) < 0.015625D && Math.abs(posZ - d2) < 0.03125D) {
            setPositionAndRotation2(posX, posY, posZ);
        } else {
            setPositionAndRotation2(d0, d1, d2);
        }
    }

    public void setPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;
    }

    /**
     * NOTE: ROTATIONS ARE ACTUALLY INUTILES BORDEL DE MERDE.
     */
    public void setPositionAndRotation2(double x, double y, double z) {
        otherPlayerMPX = x;
        otherPlayerMPY = y;
        otherPlayerMPZ = z;

        otherPlayerMPPosRotationIncrements = 3;
    }

    private int doubleToInt(final double d) {
        return (d * 32);
    }
}