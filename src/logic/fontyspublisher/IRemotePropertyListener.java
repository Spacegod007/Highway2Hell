package logic.fontyspublisher;

import java.beans.*;
import java.rmi.*;

/**
 * IRemotePropertyListener. Interface to remote property listener in order to
 * inform the listener about changes in the domain of properties that the
 * listener is subscribed to. 
 * 
 * @author Frank Peeters, Nico Kuijpers
 */
public interface IRemotePropertyListener extends IPropertyListener, Remote  {

    /**
     * Inform listener about change of a property in the domain. On the basis
     * of the data provided by the instance of PropertyChangeEvent the observer
     * is synchronized with respect to the remote domain.
     * 
     * 
     * @param evt PropertyChangeEvent @see java.beans.PropertyChangeEvent
     * @throws RemoteException
     */
    void propertyChange(PropertyChangeEvent evt) throws RemoteException;
}
