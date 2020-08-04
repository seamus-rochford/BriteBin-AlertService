//***************************************************************//
//****    DEPLOYMENT                      **********************//
//**************************************************************//

Alert Service is deployed under (both the jar and the script file are copied here)
	/apps/britebin/alerts
	
There is 1 jar file
	BriteBinAlertService.jar
	
There is a bash script file for running this alert service
	runBriteBinAlerts.sh

**************************************************************
WARNING: Don't forget to make this an executable script
	chmod +x runBriteBinAlerts.sh	
**************************************************************
	
There is 1 service defined in /etc/systemd/system
	britebin-alerts.service


This service is enabled - they start on system reboot and restart after a failure

To deploy a new version of the alert service
1. Stop the service
	systemctl stop britebin-alerts
2. Copy the file onto the server
3. Start the service
	systemctl start britebin-alerts
4. Enable the service (so it restarts on boot)
	systemctl enable britebin-alerts
5. Reload all dependencies
	systemctl daemon-reload
4. Check if service is running
	systemctl list-units --type service --all


////////////////////////////////////////////
/////// LOG FILES /////////////////////////
///////////////////////////////////////////
Note: log files for alerts can be found in /apps/britebin/alerts/logs


