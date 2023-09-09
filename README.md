<div align="center">

# Bellhop

<p>
  <b>An addon adding slack / teams notifications to Solidworks PDM Professional.</b>
  <br/>
</p>

[![](https://img.shields.io/github/license/iitmotorsports/bellhop)](https://github.com/iitmotorsports/bellhop/blob/main/LICENSE)
[![](https://img.shields.io/github/languages/code-size/iitmotorsports/bellhop)](https://github.com/iitmotorsports/bellhop)
</div>

## Configure Solidworks PDM

### Switch notification system to SMTP

1. Open `Solidworks PDM Administration`
2. Right click `Message System`
3. In general, set `Selected mail system` to `SMTP`.
4. Under SMTP Settings, set the following values
    * Server: `127.0.0.1`
    * Port: `25000`

### Configure Notification Templates

1. Open `Notification Templates/File Notifications`
2. Right-click `Changed State` and click open.
3. Select Template as `custom`
4. Set the Body to:

```
[%repeatblock_begin%{"user":"%user%","source":"%source_state%","dest":"%deststatus%","file":"%filename%"},%repeatblock_end%{}]
```

### Add Virtual Slack User

1. Open Users and Groups Management
2. Right-click `Users` and click New User
3. Click `New SOLIDWORKS PDM User`
4. Name the user `slack`
5. Set the E-mail to `slack@bellhop.iitmotorsports.org`
6. Click next, then save the user.

### Configure workflow

1. Open the default workflow
2. Open the properties for each transition (arrow)
3. Go to the notifications tab
4. Click `Add Folder Notification`
5. Select the root vault folder
6. Under Recipients, click `Add Users/Groups`, then select the slack user.
7. Click Okay to close all windows.

## Deploy bellhop

Deployment is out of the scope of this documentation since this is assumed to be an internal tool. Please deploy the
java server however you prefer.

1. Configure bellhop's properties under `application.properties`
2. Run `./gradlew bootJar`
3. Copy the jar file from `./build/libs` and run it on the PDM server.