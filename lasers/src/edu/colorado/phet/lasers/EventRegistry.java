/**
 * Class: EventRegistry
 * Package: edu.colorado.phet.lasers
 * Author: Another Guy
 * Date: Oct 29, 2004
 */
package edu.colorado.phet.lasers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Class: EventRegistry
 * Package: edu.colorado.phet.lasers
 * Author: Ron LeMaster
 * Date: Oct 28, 2004
 */

public class EventRegistry {

    public final static EventRegistry instance = new EventRegistry();

    // Key: event types
    // Value: list of listener types that handle the key event
    Map eventTypeToListenerTypeMap = new IdentityHashMap();
    // Key: listener type
    // Value: list of listeners of the key type
    Map listenerTypeToListenersMap = new IdentityHashMap();
    // Key: event type
    // Value: maps that associate listener types with the methods they use for handling the key event
    Map eventTypeToInvocationMethodMap = new IdentityHashMap();

    /**
     * Registers a listener for all events for which it has handlers. A handler is recognized
     * by a name ending in "Occurred" and having a single parameter of type assignable to
     * EventObject.
     *
     * @param listener
     */
    public void addListener( EventListener listener ) {
        Class listenerType = listener.getClass();
        Method[] methods = listenerType.getMethods();
        for( int i = 0; i < methods.length; i++ ) {
            Method method = methods[i];
            if( /*method.getName().endsWith( "Occurred" )
                &&*/ method.getParameterTypes().length == 1
                     && EventObject.class.isAssignableFrom( method.getParameterTypes()[0] ) ) {

                // Register the listener on the event type in the method's signature
                registerListenerForEvent( listener, method );
            }
        }
    }

    /**
     * Removes a listener from the registry
     *
     * @param listener
     */
    public void removeListener( EventListener listener ) {
        Class listenerType = listener.getClass();
        Set set = (Set)listenerTypeToListenersMap.get( listenerType );
        set.remove( listener );
    }

    private void registerListenerForEvent( EventListener listener, Method method ) {
        Class eventType = method.getParameterTypes()[0];
        if( !eventTypeToInvocationMethodMap.containsKey( eventType ) ) {
            eventTypeToInvocationMethodMap.put( eventType, new HashMap() );
        }
        Map m = (Map)eventTypeToInvocationMethodMap.get( eventType );
        Method testMethod = (Method)m.get( listener.getClass() );
        if( testMethod == null ) {
            m.put( listener.getClass(), method );
        }

        // Put the listener in the map keyed by its type
        if( !listenerTypeToListenersMap.containsKey( listener.getClass() ) ) {
            listenerTypeToListenersMap.put( listener.getClass(), new HashSet() );
        }
        ( (Set)listenerTypeToListenersMap.get( listener.getClass() ) ).add( listener );

        // If the listeners type isn't already identified with the event type,
        // make the association
        if( !eventTypeToListenerTypeMap.containsKey( eventType ) ) {
            eventTypeToListenerTypeMap.put( eventType, new HashSet() );
        }
        Set listenerTypes = (Set)eventTypeToListenerTypeMap.get( eventType );
        listenerTypes.add( listener.getClass() );
    }

    /**
     * Causes all listeners registered for a specified event to be notified through
     * their registered methods
     *
     * @param event
     */
    public void fireEvent( EventObject event ) {
        Set listenerTypeList = (Set)eventTypeToListenerTypeMap.get( event.getClass() );
        if( listenerTypeList != null ) {
            for( Iterator iterator0 = listenerTypeList.iterator(); iterator0 != null && iterator0.hasNext(); ) {
                Class listenerType = (Class)iterator0.next();
                Set listeners = (Set)listenerTypeToListenersMap.get( listenerType );
                for( Iterator iterator = listeners.iterator(); iterator != null && iterator.hasNext(); ) {
                    EventListener listener = (EventListener)iterator.next();
                    Map methodMap = (Map)eventTypeToInvocationMethodMap.get( event.getClass() );
                    Method method = (Method)methodMap.get( listenerType );
                    try {
                        method.invoke( listener, new EventObject[]{event} );
                    }
                    catch( IllegalAccessException e ) {
                        e.printStackTrace();
                    }
                    catch( InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
