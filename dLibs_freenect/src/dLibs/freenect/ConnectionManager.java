package dLibs.freenect;

import dLibs.freenect.KinectCore.Core;
import dLibs.freenect.interfaces.Connectable;
import dLibs.freenect.interfaces.Logable;

abstract class ConnectionManager implements Connectable, Logable {

  private Kinect parent_kinect_ = null;

  protected ConnectionManager(){}
  
  @Override
  public final int getIndex() {
    return (parent_kinect_ != null) ? parent_kinect_.getIndex() : -1;
  }
  
  //----------------------------------------------------------------------------
  // CONNECT
  @Override
  public final void connect( Kinect parent_kinect ){
    if( parent_kinect == null )
      return;
    if( this.parent_kinect_ != parent_kinect){     // only if the new kinect is not the old kinect
      this.disconnect();                           // destroy  possible old connections 
      this.parent_kinect_ = parent_kinect;         // register the new kinect
      parent_kinect.connect(this);                 // let the new kinect do its work, to connect the current plugin to it
      connectCallback();                           // call callback function
      parent_kinect_.updateEvents();

    }
  }
  //----------------------------------------------------------------------------
  // DISCONNECT
  @Override
  public final void disconnect(){
    if( this.parent_kinect_ != null ){
      disconnectCallback();
      this.parent_kinect_.disconnect(this.getClass());
      this.parent_kinect_ = null;
    }
  }
  
  @Override
  public final Kinect isConnected(){
    return parent_kinect_;
  }
  
  protected final Core getCore(){
    return ( parent_kinect_ != null ) ? this.parent_kinect_.getCore() : null;
  }
  
  //TODO: manage that all stop their threads!!
  protected abstract void disconnectCallback();
  protected abstract void connectCallback();
  
  

  //----------------------------------------------------------------------------
  @Override
  public String toString(){
    String class_name_ = this.getClass().getSuperclass().getSimpleName();
    if ( this.getClass().getSuperclass() == Object.class)
      class_name_ = this.getClass().getSimpleName();
    
    String name  = String.format("%-16s", class_name_ );
    String index = String.format("%2d",   this.getIndex());
    String code  = String.format("@%-7s", Integer.toHexString(this.hashCode()));
    String note  = String.format("%6s",   (KinectCore.hasDevice(getCore())) ? "active":"");
    return "("+name+ " . index:" + index+" . "+code+" . "+note+")";
  }
  

}