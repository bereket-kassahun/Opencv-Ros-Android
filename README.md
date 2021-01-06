# Opencv-Ros-Android
  Image processing and ROS(Robot Operating System) combined in one android application
========================================================================================
After installing the app you need to install ros environment on you pc, then start your master with
ROS_MASTER_URI="your ip address(192.168.0.101)", enter the same ip on the first Activity that is shown
on the app.
This application was used in robocup for ball, marker and goal detection. It works as follows:
        * face your back camera to the object(Color) you want to detect and touch it three 
           times, it actually draws a circle when you do this, i have done this just to take the circle
           as a ball and use it's radius for other computation on the robot.
        * again face your back camera to the second color and touch it three times 
        * do it again for the third color
With little modifications you can do many things with it, but as you detected more and more colors 
the app begins to slow down.


