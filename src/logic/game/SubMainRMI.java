package logic.game;

import views.GameView;
import javafx.application.Platform;
import logic.fontyspublisher.IRemotePropertyListener;
import logic.fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listener class of the game server (client side)
 */
public class SubMainRMI extends UnicastRemoteObject implements Serializable, IRemotePropertyListener
{
    private static final Logger LOGGER = Logger.getLogger(SubMainRMI.class.getName());

    /**
     * The class where the game is being displayed
     */
    private final transient GameView application;

    /**
     * The constructor of the listener class
     * @param application (client) where the game is being played
     * @param rpl where this class is listening to
     * @throws RemoteException if something goes wrong while setting up the remote connection
     */
    public SubMainRMI(GameView application, IRemotePublisherForListener rpl) throws RemoteException
    {
        this.application = application;
        /*
      The object which this class listens to
     */
        try
        {
            rpl.subscribeRemoteListener(this, "gameObjects");
            rpl.subscribeRemoteListener(this, "allDead");
        } catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, "Error in connection", e);
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        String s = evt.getPropertyName();
        if (s.equals("gameObjects"))
        {
            Platform.runLater(() -> application.update((List<GameObject>) evt.getNewValue()));
        }
        else if (s.equals("allDead"))
        {
            Platform.runLater(application::setScores);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
