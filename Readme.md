# News Portal

### Installation:

1) Install Docker\
   Choose your OS and follow instructions
    - for windows:
   https://docs.docker.com/desktop/windows/install/
    - for linux (select your distribution from the table):
   https://docs.docker.com/engine/install/
    - for mac: 
   https://docs.docker.com/desktop/mac/install/
2) Open terminal (for Windows: Win+R -> cmd -> Enter)
3) Copy the following line and press Enter\
`docker pull goosepusher/news-portal:1.0.0`
4) After line "Digest: sha-256: ..."\
Copy the following line and again press Enter \
`docker run --name news-portal -p 7228:7228 -d goosepusher/news-portal:1.0.0`
5) Congratulations! You can find your site on: http://localhost:7228/ \
or http://127.0.0.1:7228/

### Basic work with your server:
1) Stop server (without deleting data)\
`docker stop news-portal`
2) Restart stopped server\
`docker start news-portal`
3) Remove stopped server (you will lose all your articles!)\
`docker rm news-portal`
4) Remove server code from your computer:\
`docker rmi goosepusher/news-portal:1.0.0`

### Main features:
 + Application is designed using REST architecture
 + Saved articles won't disappear after a server shutdown 
 + Articles are shown on separate pages
 + Sort by article theme is available
 + Every value from client is validated on server
 + JUnit tests are present for every endpoint
 + HTML, JS, SQL injections are not possible

P.S. Docker Hub:\
https://hub.docker.com/r/goosepusher/news-portal
