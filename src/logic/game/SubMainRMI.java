package logic.game;

import bootstrapper.Main;
import javafx.application.Platform;
import logic.fontyspublisher.IRemotePropertyListener;
import logic.fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Listener class of the game server (client side)
 */
public class SubMainRMI extends UnicastRemoteObject implements Serializable, IRemotePropertyListener
{
    /**
     * The class where the game is being displayed
     */
    private final transient Main application;

    /**
     * The constructor of the listener class
     * @param application (client) where the game is being played
     * @param rpl where this class is listening to
     * @throws RemoteException if something goes wrong while setting up the remote connection
     */
    public SubMainRMI(Main application, IRemotePublisherForListener rpl) throws RemoteException
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
            e.printStackTrace();
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        switch (evt.getPropertyName())
        {
            case "gameObjects":
                Platform.runLater(() -> application.update((List<GameObject>)evt.getNewValue())); break;
            case "allDead":
                Platform.runLater(application::setScores);
        }
    }
}
