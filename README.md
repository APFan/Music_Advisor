# Music_Advisor
A simple MVC Spotify client written using it's API without any additional http libraries.
To use you have to create a config.properties file with "clientID" and "clientSecret" in the resources folder. You can use -access, -reource and -page command line parameters to specify the paths which will be used for requesting the token and API, and the size of a search result page.

To authorize type auth and follow instructions, after authorization you can use the following commands: "new" to show new releases, "featured" to show featured playlists, "categories" to show playlist categories and "playlists *CATEGORY NAME*" to show the playlists in that category.
