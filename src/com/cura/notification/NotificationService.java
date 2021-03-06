/*
 Copyright© 2010, 2011 Ahmad Balaa, Oday Maleh

 This file is part of Cura.

	Cura is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Cura is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Cura.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cura.notification;

/*
 * Description: While still logged into a server through Cura, the user will be notified whenever a new user has just logged into 
 * that very server. The settings for this feature are in the Settings of course, and the refresh intervals for checking for new 
 * log-ons is 30 seconds, 3, 5 and 10 minutes.
 */

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.cura.Connection.CommunicationInterface;
import com.cura.Connection.ConnectionService;
import com.cura.Connection.SSHConnection;

public class NotificationService extends Service {

 private CommunicationInterface conn;
 SSHConnection sshconnection;

 private ServiceConnection connection = new ServiceConnection() {
  public void onServiceConnected(ComponentName arg0, IBinder service) {
   conn = CommunicationInterface.Stub.asInterface(service);
  }

  public void onServiceDisconnected(ComponentName name) {
   conn = null;
  }
 };

 public String sendAndReceive(String command) {
  String result = "";

  try {
   result = conn.executeCommand(command);
  }
  catch (RemoteException e) {
   Log.d("SysLog", e.toString());
  }
  return result;
 }

 public void doBindService() {
  Intent i = new Intent(this, ConnectionService.class);
  bindService(i, connection, Context.BIND_AUTO_CREATE);
 }

 private final NotificationInterface.Stub mBinder = new NotificationInterface.Stub() {
  public synchronized String executeCommand(String command) throws RemoteException {
   String result = "";
   try {
	result = sshconnection.messageSender(command);
   }
   catch (Exception e) {
	Log.d("ConnectionService", e.toString());
   }
   return result;
  }
 };

 @Override
 public void onDestroy() {
  super.onDestroy();
 }

 @Override
 public IBinder onBind(Intent intent) {
  return null;
 }

}
