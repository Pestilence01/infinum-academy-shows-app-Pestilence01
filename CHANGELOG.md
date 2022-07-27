This file will list all the changes and improvements developed over a period of 8 assignments


Changelog:

Assignment1: 
	This is the very beginning of the project which was created with only one empty activity which consists of one textview inside a constraint layout which says "Hello World!"
	
Assignment2:
	-Created custom icon
	-Changed the application name to "Shows"
	-Adjusted the .gitignore to ignore every element in the .idea folder
	-Created LoginActivity:
			-Added every element in ScrollView so if the UI is too large for a device, the user can scroll with no problem and everything should be scaled correctly during the user input (when he keyboard appears)
			-Login Button logic:
					-Defined custom appearance for two states in .xml (one for the Enabled and one for the Disabled state)
					-It is disabled by default and email and password field implement an on change listener so whenever the fields fulfill the criteria, it will turn to enabled
					-Criteria for button to be enabled:
							-Email field is not empty
							-Password is at least 6 characters long
					-When the login button is enabled, the onClickListener will match the email field with the ".*@.*" regex pattern:
																		-if it doesn't match, the error will be displayed to input the correct email
																		-if it does match, an IMPLICIT intent will start the WelcomeActivity with the email as a string extra
	-Created WelcomeActivity:
			-Constraint Layout that displays the app logo in the centre and welcomes the user
			
	-Every hardcoded dimension and value is put to its respective file (dimens.xml , strings.xml , colors.xml)
		
					
