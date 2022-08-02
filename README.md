# distribuited-systems-project-2022
Peer 2 Peer centralized file sharing system


To-do:
- [ ] Treat Join request from a Peer already in the system
- [ ] File directory not found
- [x] Repeat on fail (JOIN, LEAVE, UPDATE, ALIVE, SEARCH)
- [x] Peer denying request
- [x] Peer checking for file before replying
- [x] After being rejected try another
- [x] Download complete message
- [x] Rejected download message
- [ ] Add comments to code
- [ ] Refactor do only have Servidor, Peer and Message
- [ ] Take care of duplicates on the server
- [ ] Fix memory leaks
- [ ] Report (11201721028.pdf)
	- Name and ID
	- Link for video
		- 3min max
		- Compilation
		- Operation
	- Explanation of each funcionality for the server and peer (mentioning the lines on the code)
	- Explanation of each Thread
	- Mention references (Stack overflow and Redes de Computadores)

proteger servidor de peers que ele não conhece (caiu e voltou -> o peer vira um leecher)
(Peer não sabe que saiu)

Known issues:
	file names with spaces in it
	UDP alive uses another port with +1 value (could overflow or make weird bugs)
	No "Unsuccessful messages" so there's no way to know if the server actually did something
	Could do byte operations but chose to have a DownloadAllowed message

JOIN 127.0.0.1 3483 ./files1

JOIN 127.0.0.1 4395 ./files2

SEARCH franceEspacoQuebra.mp4

DOWNLOAD 127.0.0.1 3483
