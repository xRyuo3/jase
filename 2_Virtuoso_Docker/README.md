### Links

- https://dbpedia.org/sparql
- https://www.w3.org/2009/Talks/0615-qbe/
- https://github.com/dbpedia/virtuoso-sparql-endpoint-quickstart
- https://medium.com/virtuoso-blog/dbpedia-basic-queries-bc1ac172cc09
- https://vos.openlinksw.com/owiki/wiki/VOS/VirtRDFInsert
- http://rdfplayground.dcc.uchile.cl/
- https://github.com/tenforce/docker-virtuoso

### Setup

```
git clone https://github.com/dbpedia/virtuoso-sparql-endpoint-quickstart.git
cd virtuoso-sparql-endpoint-quickstart
```

Change in `.env`

```
VIRTUOSO_ADMIN_PASSWD=SecretPassword
COLLECTION_URI=https://databus.dbpedia.org/dbpedia/collections/virtuoso-sparql-endpoint-quickstart-preview
```

=> New repo created

```
$ tree -a -I .git

.
├── dbpedia-loader
│   ├── Dockerfile
│   └── import.sh
├── docker-compose.yml
├── .env
├── .gitignore
└── README.md

2 directories, 6 files
```

Quickstart

```
docker-compose up
```

See on `localhost:8890/sparql`

Docker command to kill everything

```
docker kill $(docker ps -aq); docker rm $(docker ps -aq)
```
