# MequieChat - A WhatsApp clone

##  Grupo 37
* 51021 - Pedro Marques
* 51110 - Marcelo Mouta
* 51468 - Bruno Freitas

## RUN
Abra o terminal dentro da pasta 'SegC-grupo37-proj2' para executar os próximos comandos.
Pode deslocar-se para lá fazendo:
```
cd $HOME/SegC-grupo37-proj2
```

### BUILD

Para compilar o projeto temos de executar o script bash build.sh
Antes de executar aconselhamos a executar primeiro:
```
chmod +x ./build.sh
```
e finalmente:
```
./build.sh
```

### RUN

Para correr o programa com os ficheiros de permissões corretamente é necessario e que este se encontre localizado na home do utilizador: $HOME/SegC-grupo37-proj1-2

#### Servidor:
```
java -Djava.security.manager -Djava.security.policy=server/server.policy -cp server/bin/ mequie.main.MequieServer <port> <keystore> <keystore-password>
```
#### Cliente:
```
java -Djava.security.manager -Djava.security.policy=client/client.policy -cp client/bin/:server/bin/ mequie.main.Mequie <serverAddress> <truststore> <keystore> <keystore-password> <localUserID>
```
## Detalhes de implementação
![Drag Racing](Mequie.png)
```java
//TODO
```
## Gestão de dados cifrados (persistência em disco)

### Servidor
* Organização: O servidor contem os dados na pasta Data.
Dentro do Data existe dois ficheiros base do servidor:
    * `groups.txt`: contem todos os grupos e quem pertence ao grupo, sendo o owner do grupo o primeiro utilizador a aparecer
    * `passwd.txt`: contem o utilizador e a sua password <username>:<password>
Cada grupo contem a sua pasta dentro do Data (Data/<group>) que contem 2 ficheiros base + photos:
    * `messages_info.txt`: todas as mensages (photos ou texto) enviadas no grupo, contendo o ID, se eh p (photo) ou t (texto) e quem falta ler
    * `text_messages.txt`: todas as mensagens de texto enviadas no grupo, o seu ID, quem enviou e o conteúdo
    * Irá ter todas as photos que forem enviadas para o grupo e que nao tenham sido vistas por todos os utilizadores do grupo

Com esta organização, o load do sistema e as operacoes de escrita em disco sao mais eficientes mas a memória ocupada é maior por haver conteúdo repetido.


## Comunicação segura e limitações

### LIMITAÇÕES

#### Cliente:

O cliente apenas reconhece os seguintes comandos/atalhos:
    create/c
    addu/a
    removeu/r
    ginfo/g
    uinfo/u
    msg/m
    photo/p
    collect/co
    history/h
    exit

* Para enviar uma foto é preciso indicar o filepath da mesma, com este pertencendo ao SegC-grupo37-proj1/ já que apenas é permitido ler aí pela policy. Apenas se aceitam ficheiros até 2GB.

* Ao fazer collect, as fotos serão colocadas num diretório 'ClientData' sobre uma pasta com o nome do grupo onde se enviou essa foto, que será gerado no diretório, de dados,principal do programa.

#### Servidor:

* Não é possível eliminar grupos.
