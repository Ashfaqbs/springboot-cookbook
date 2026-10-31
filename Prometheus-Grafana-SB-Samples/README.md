# Monitoring Spring Boot Projects with Prometheus and Grafana

## Prometheus

### What is Prometheus?

Prometheus is an open-source monitoring and alerting toolkit designed for reliability and scalability. It is capable of collecting metrics from various sources, storing them, and providing powerful querying and visualization capabilities.

### History

Prometheus was created at SoundCloud in 2012 and later open-sourced in 2015. It has since gained widespread adoption in the DevOps and monitoring communities due to its simplicity and effectiveness.

### Why Use Prometheus in Spring Boot?

**Advantages**:
- **Service Discovery**: Prometheus can dynamically discover and monitor new instances of Spring Boot applications as they come online.
- **Powerful Querying**: Its querying language, PromQL, allows for flexible and powerful querying of metrics.
- **Alerting**: Prometheus can send alerts based on defined conditions, helping you proactively address issues.
- **Data Retention**: You can configure data retention policies to store metrics for different time periods.

### Real-Time Scenario

Imagine a scenario where you have a microservices architecture built with Spring Boot, and you need to monitor the performance and health of your services:

- **Service Discovery**: As new microservices instances are deployed, Prometheus automatically discovers and starts collecting metrics from them.
- **Metrics Collection**: Prometheus collects metrics like response times, error rates, and resource usage from each service.
- **Alerting**: If any service's error rate exceeds a certain threshold, Prometheus triggers an alert, notifying the operations team.
- **Visualization**: Grafana, integrated with Prometheus, provides real-time dashboards that allow developers and operations teams to monitor the entire system's health and performance.

## Grafana

### What is Grafana?

Grafana is an open-source platform for monitoring and observability. It provides rich visualization, dashboarding, and alerting for various data sources, including Prometheus.

### History

Grafana was initially released in 2014 and has since become a widely adopted solution for creating visually appealing and interactive dashboards for monitoring and observability.

### Why Use Grafana in Spring Boot?

**Advantages**:
- **Flexible Dashboards**: Grafana offers a flexible and customizable dashboarding interface, allowing you to create informative and visually appealing dashboards.
- **Alerting and Notification**: Grafana supports alerting and notification channels, helping you respond to incidents promptly.
- **Data Source Integration**: Grafana seamlessly integrates with various data sources, including Prometheus, making it an excellent choice for visualizing Prometheus metrics.

### Real-Time Scenario

In a real-time scenario, you can use Grafana to create dashboards that visualize the metrics collected by Prometheus:

- **Dashboard Creation**: Build custom dashboards that show real-time data for CPU usage, memory, response times, and more.
- **Threshold Alerts**: Set threshold alerts on dashboards to get notified when a metric exceeds predefined limits.
- **Historical Analysis**: Grafana allows you to compare historical data with current metrics, helping you identify trends and anomalies.
- **Collaboration**: Share dashboards with your team to facilitate collaboration and provide visibility into the system's health.

By combining Prometheus and Grafana, you can effectively monitor, analyze, and optimize your Spring Boot applications, ensuring their reliability and performance.


In this guide, we will walk through the process of setting up monitoring for two Spring Boot projects using Prometheus and Grafana. The projects are:

1. **Project 1: API with Performance Testing**
   - Monitoring heap memory usage
   - Monitoring garbage collection
   - Monitoring non-heap memory
   - Monitoring CPU usage
   - Running performance tests
   - Setting up Prometheus and Actuator

2. **Project 2: CRUD Application**
   - Implementing proper PUT method
   - Integrating Log4j for logging
   - Creating a CRUD API for Cycle objects
   - Setting up Prometheus and Actuator

 ## Create Springboot Projects and Prometheus and Actuator dependency 

       <dependency>
			<groupId>io.micrometer</groupId>
			<artifactId>micrometer-registry-prometheus</artifactId>
			<scope>runtime</scope>
	   	</dependency>
     <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
	  	</dependency>
 ### Enabling the actuator in springboot 
 In application.properties add
     
     management.endpoints.web.exposure.include=*
     management.endpoint.health.show-details=always


## Setup Prometheus
go to https://prometheus.io/ and download prometheus(ZIP) for the respective operating system once downloaded in prometheus.yml
configure the our springboot project note: make sure of intendation else while starting up prometheus.exe it will throw error and 
by default prometheus.yml has prometheus job configured 
in this we will define a custom job name for each project , actuator/prometheus path , application base url , scrape data time eg: here 3 seconds

    - job_name: 'my-spring-boot-project'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 3s
    static_configs:
    - targets: ['localhost:8080']
     
     
    - job_name: 'my-sb-project-DB'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 3s
    static_configs:
    - targets: ['localhost:8081']

![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/fd95e888-dfd9-437e-89dd-11b682b590a9)


once config is done start the prometheus 
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/7d2e003b-412c-4d04-b382-80fc1565699c)
to start we can use gitbash , CMD , or window powershell , call the prometheus.exe
            
             PS C:\Users\Ashfaq PC\desktop\Springboot\prometheus-2.47.0-rc.0.windows-amd64> .\prometheus.exe

### once done the prometheus will run on 9090 port and prometheus will be using the application data from actuators as we have defined the acutator url in congif.yml file of prometheus


![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/366aaa86-e177-44d9-9069-d323fd9777df)

here we can do the qeury i.e promql for each job i.e which we have defined in .yml config , instance i.e (port)
eg:
if not getting which metric to use for your application 
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/decd75ea-3596-451c-afe2-adaa13ee319b)
add this query and all the metrics will be displayes and select that will be showed in table or graph format
 simple query : noe here if sum()is not working please remove and try 
 
 sum(rate(http_server_requests_seconds_count{status!="404"}[1m]))
 rate(http_server_requests_seconds_count{status!="404"}[1m]

          HTTP Request Metrics:

    Total Request Rate: Monitoring the overall request rate.
      sum(rate(http_server_requests_seconds_count{status!="404"}[1m]))
       Request Rate by Status Code: Breakdown of request rates by HTTP status codes (e.g., 2xx, 4xx, 5xx).
       sum(rate(http_server_requests_seconds_count{status=~"2..|4..|5.."}[1m])) by (status)
        Response Time: Monitoring the average response time of HTTP requests.
        avg(rate(http_server_requests_seconds_sum{status!="404"}[1m])) by (method)


     Application Health:
     
     Application Uptime: Monitoring the application's uptime.
     up{job="my-sb-project-DB", instance="localhost:8081"}
      
      Memory Usage: Monitoring the JVM heap memory usage.
       jvm_memory_used_bytes{area="heap", instance="localhost:8081", job="my-sb-project-DB"}
    
      CPU Usage: Monitoring the CPU usage of the application process.
     process_cpu_usage{instance="localhost:8081", job="my-sb-project-DB"}

     Database Metrics (if applicable):
     
     Database Connection Pool Usage: Monitoring the usage of the database connection pool (e.g., HikariCP).
    hikaricp_connections_active{instance="localhost:8081", job="my-sb-project-DB", pool="HikariPool-41"}

    Database Query Performance: Monitoring the execution time of database queries.
    sum(rate(jdbc_query_duration_seconds_count{job="my-sb-project-DB"}[1m])) by (query)

    
     
     System Metrics:

     CPU Usage: Monitoring the system's CPU usage.
    system_cpu_usage{instance="localhost:8081", job="my-sb-project-DB"}

    Memory Usage: Monitoring the system's memory usage.
    node_memory_MemTotal_bytes{instance="localhost:8081"}

     Disk Space Usage: Monitoring disk space usage.
     node_filesystem_size_bytes{instance="localhost:8081", mountpoint="/"}



We can add new panel too in the search page and to see the job/targets it will show all the jobs , with their ports
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/62baca1b-885a-4bd7-84f0-ad24dc565bb9)

![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/8f1406e1-c7a5-4ced-b5fb-ed364fbe6f25)

![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/ba6d0df3-f297-48f8-8b67-dff8142ca07e)

### any application is down it will show in the target section only


## Setting up Grafana https://grafana.com/grafana
As this prometheus provides a basic graph, to use more presentable dashboard, access management of dashboard we will use grafana 
after downloading the grafana we need to run this as above how we have ran 
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/79e22f60-16df-47f3-b96e-fa4e857c20c1)
note the default port of grafana is 3000 

### setting up grafana 
once we login this application default username and password is admin and admin, can change later 
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/320b4903-d8cf-407c-a245-6e407cc3be2a)

now we need to provide a data i.e datasource so select connection 
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/df338693-31bd-4603-b0e4-0912a37ada7a)
and select prometheus as datasource 
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/7e43c367-fadf-4f77-986f-bf01a832e70a)
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/1947db81-7d24-49fc-9b4f-93c8bd9739da)

name the connection default Prometheus
and provide the URL of the prometheus
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/59dda4a5-feb2-4ca5-a435-87735491af2d)

![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/d27252f8-4ea8-4232-b8c1-7aa24f26e539)
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/5265b865-242d-4687-a580-bfbaac9ad160)

once done next build a dashboard for the defined datasource, here we can define prometheus twice as datasource
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/ea30f55a-6319-46b7-a779-4013b6007af4)
select build a dashboard
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/0de45513-55a9-4c3f-a97e-6f730905cc62)
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/2ac63cf1-8d74-4579-a795-d3e1ffe64264)
select datasource as your prometheus defined,and here on right side we can select what type of visual we want and it will showed on panel
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/fd4906ba-631b-4c52-b908-17531cf63966)
and below we can also define hardcoded and dynamic queries too 
eg:
hardcoded we can use job or instance i.e base url
 sum(rate(http_server_requests_seconds_count{job="my-sb-project-DB", status!="404"}[1m])) by (method) promQL which will give all the http methods count 
up{job="my-sb-project-DB", instance="localhost:8081"} application status up or not
dynamic
rate(logback_events_total{instance=~"$instance", application="$application", level="warn"}[5m])
# rate(logback_events_total{instance=~"$instance", application="$application", level="error"}[5m])

what ever we select in dashboard instance , it will be showed

![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/73b09d2d-453a-41b0-bed1-3578f8a9ef73)
application tab im not sure , but we can see fo each project by their instance i,e base url 
### dashboard preview 
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/318a8740-2546-4646-a195-b4f4d713f92c)

![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/de60e4f7-867a-4a7e-966a-4df3d32d4756)

![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/aa9b2519-2cd2-4faf-a0a8-4acc7a409848)
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/0fbf4500-648e-47ee-a035-88ab1546f5ed)
![image](https://github.com/DarkSharkAsh/PrometheusXGrafana-SB/assets/105435085/72afa572-ef77-4e26-834d-3a3080e2c516)
logger used only info , warn ,debug was not printing in application itself and we can also setup alerts need too 



















     
