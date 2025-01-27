# Distributed_Web_Scraper
This System provided is for demonstration of distributed system using distributed task scheduler,so the main aim was to demo the task scheduler but since it was implicitly working in the background we wanted to show using the web scraping usecase,so here you could get different classes having particular functionalities related to the following description:

#MasterNode-
This class have got the necessary methods to get the url and pass to get the size of content to be scraped.
Gets the size fetched and schedules the urls inserted using the size,using a queue it implements scheduling for execution.
Their is youtube's url that we have used to show case as a high priority so for the domains of youtube the scheduler gives first priority of execution(web scraped).
