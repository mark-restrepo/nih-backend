# NIH PubMed Search

This is a simple application that implements an API for searching all of [NIH pubmed](https://pubmed.ncbi.nlm.nih.gov). The app provides two endpoints:

1. `/search/{term}`, which submits a search to the service and returns an id and expected number of records.
2. `/fetch/{task_id}`, which retrieves search results. 

## Setup
### Development
To install, clone the repo and install with [maven](https://maven.apache.org/): 

```bash
git clone git@github.com:mark-restrepo/nih-backend.git
cd nih-backend 
mvn clean install
```
 
### Deployment

To run the application, clone the repo, and create the shaded jar:

```bash
git clone git@github.com:mark-restrepo/nih-backend.git
cd nih-backend 
mvn clean package
```

Then, the app can be run with this command:

```bash 
java -jar target/nih-1.0-SNAPSHOT.jar 
```

If everything was successful, you should be able to access the api at http://localhost:7070
