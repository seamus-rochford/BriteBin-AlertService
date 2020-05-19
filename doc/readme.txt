//***************************************************************//
//****    DEPLOYMENT                      **********************//
//**************************************************************//

Alert Service is deployed under 
	/apps/britebin/alerts
	
There are 1 Service
	BriteBinAlertService.jar
	
There are a bash script files for running these listener
	runBriteBinAlertService.sh
	
There are 1 services defined in /etc/systemd/system
	britebin-alert.service


This services is enabled - it will start on system reboot and restart after a failure

To deploy a new version of a listener service
1. Stop the service
	systemctl stop britebin-alert
2. Copy the file onto the server
3. Start the service
	systemctl start britebin-alert
4. Enable the service (so it restarts on boot)
	systemctl enable britebin-alert
5. Reload all dependencies
	systemctl daemon-reload
4. Check if service is running
	systemctl list-units --type service --all


////////////////////////////////////////////
/////// LOG FILES /////////////////////////
///////////////////////////////////////////
Note: log files for can be found in /apps/britebin/alerts/logs
