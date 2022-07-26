/**
 * Created by TuanPM (tuanpm@live.com) on 1/4/16.
 */

package com.tuanpm.RCTMqtt;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.util.HashMap;
import java.util.UUID;

public class RCTMqttModule extends ReactContextBaseJavaModule
{

    private static final String TAG = "RCTMqttModule";
    private final ReactApplicationContext reactContext;
    private HashMap<String, RCTMqtt> clients;

    public RCTMqttModule(ReactApplicationContext reactContext)
    {
        super(reactContext);
        this.reactContext = reactContext;
        clients = new HashMap<>();
    }

    @Override
    public  String getName()
    {
        return "Mqtt";
    }

    @ReactMethod
    public void createClient(final ReadableMap options,
                             Promise promise)
    {
        final String clientRef = createClientRef();
        RCTMqtt client = new RCTMqtt(clientRef, reactContext, options);
        client.setCallback();
        clients.put(clientRef, client);
        promise.resolve(clientRef);
        log(new StringBuilder("ClientRef:").append(clientRef).toString());
    }

    @ReactMethod
    public void connect( final String clientRef)
    {
        clients.get(clientRef).connect();
    }

    @ReactMethod
    public void disconnect( final String clientRef)
    {
        clients.get(clientRef).disconnect();
    }

    @ReactMethod
    public void disconnectAll()
    {
        if (clients != null && clients.size() > 0) {
            for (RCTMqtt aClient : clients.values()) {
                aClient.disconnect();
            }
        }
    }

    @ReactMethod
    public void subscribe( final String clientRef,
                           final String topic,
                          final int qos)
    {
        clients.get(clientRef).subscribe(topic, qos);
    }

    @ReactMethod
    public void unsubscribe( final String clientRef,
                            final  String topic)
    {
        clients.get(clientRef).unsubscribe(topic);
    }

    @ReactMethod
    public void publish( final String clientRef,
                         final String topic,
                         final String payload,
                        final int qos,
                        final boolean retain)
    {
        clients.get(clientRef).publish(topic, payload, qos, retain);
    }

    @ReactMethod
    public void removeClient( final String clientRef) {
        if (!clients.containsKey(clientRef))
        {
            return;
        }
        log(new StringBuilder("Remove client ").append(clientRef).toString());
        clients.remove(clientRef);
    }

    @ReactMethod
    public void reconnect( final String clientRef)
    {
        clients.get(clientRef).reconnect();
    }
    
    @ReactMethod
    public void isConnected( final String clientRef, Promise promise)
    {
        promise.resolve(clients.get(clientRef).isConnected());
    }

    @ReactMethod
    public void getTopics( final String clientRef, Promise promise)
    {
	promise.resolve(clients.get(clientRef).getTopics());
    }

    @ReactMethod
    public void isSubbed( final String clientRef, String topic, Promise promise)
    {
	promise.resolve(clients.get(clientRef).isSubbed(topic));
    }

    private String createClientRef()
    {
        return UUID.randomUUID().toString();
    }

    private void log( final String message)
    {
        if (!BuildConfig.DEBUG)
        {
            return;
        }
        Log.d(TAG, message);
    }

    @Override
    public void onCatalystInstanceDestroy() {
        disconnectAll();
    }

}
