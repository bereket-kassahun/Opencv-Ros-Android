package com.example.finaltrial;


import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import java.text.SimpleDateFormat;
import java.util.Date;

import std_msgs.UInt16;
import std_msgs.UInt16MultiArray;
import std_msgs.UInt64MultiArray;

public class PublishingNode extends AbstractNodeMain implements NodeMain {

    private static final String TAG = PublishingNode.class.getSimpleName();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("SimplePublisher/TimeLoopNode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        final Publisher<std_msgs.String> publisher = connectedNode.newPublisher(GraphName.of("time"), std_msgs.String._TYPE);

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                // retrieve current system time
                String time = ColorBlobDetectionActivity.getTile();

                //Log.i(TAG, "publishing the current information: " + time);

                // create and publish a simple string message
                std_msgs.String str = publisher.newMessage();

                str.setData(time);

                publisher.publish(str);

                // go to sleep for one second
                Thread.sleep(1000);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

}