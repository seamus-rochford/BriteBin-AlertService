//***************************************************************//
//****    DEPLOYMENT                      **********************//
//**************************************************************//

Alert Service is deployed under 
	/apps/britebin/alerts
	
There is 1 Service
	BriteBinAlertService.jar
	
There is a bash script files for running this service
	runBriteBinAlertService.sh
	
There is 1 service defined in /etc/systemd/system
	britebin-alert.service


This services is enabled - it will start on system reboot and restart after a failure

To deploy a new version of a the alert service
1. Stop the service
	systemctl stop britebin-alert
2. Copy the file onto the server
3. Start the service
	systemctl start britebin-alert
4. Enable the service (so it restarts on boot)
	systemctl enable britebin-alert
5. Reload all dependencies
	systemctl daemon-reload
6. Check the status of teh service
	systemctl status britebin-alert
7. Check if service is running
	systemctl list-units --type service --all


////////////////////////////////////////////
/////// LOG FILES /////////////////////////
///////////////////////////////////////////
Note: log files for can be found in /apps/britebin/alerts/logs
