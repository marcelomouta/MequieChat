Mequie - Trabalho de Segurança e Confiabilidade - Fase 1

Grupo 37
* 51021 - Pedro Marques
* 51110 - Marcelo Mouta
* 51468 - Bruno Freitas

Abra o terminal dentro da pasta 'SegC-grupo37proj1' para executar os próximos comandos.
Pode deslocar-se para lá fazendo:
    cd $HOME/SegC-grupo37proj1


BUILD

Para compilar o projeto temos de executar o script bash build.sh
Antes de executar aconselhamos a executar primeiro:

    chmod +x ./build.sh

e finalmente:

    ./build.sh


RUN

Para correr o programa com os ficheiros de permissões corretamente é necessario e que este se encontre localizado na home do utilizador: $HOME/SegC-grupo37proj1 

Servidor:
    java -Djava.security.manager -Djava.security.policy=server/server.policy -cp server/bin/ mequie.main.MequieServer <port>

Cliente:
    java -Djava.security.manager -Djava.security.policy=client/client.policy -cp client/bin/:server/bin/ mequie.main.Mequie <hostname:port> <username> [password]

(A password é opcional, sendo que é pedida pelo o programa caso não venha explícita nos argumentos.)


LIMITAÇÕES

Cliente:

* O cliente apenas reconhece os seguintes comandos/atalhos:
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

* Para enviar fotos é preciso colocar a foto na pasta client (policy so permite ler e escrever nessa pasta), sendo que apenas se aceitam ficheiros até 2GB.

* Ao fazer collect, as fotos serão colocadas num diretório 'ClientData' sobre uma pasta com o nome do grupo onde se enviou essa foto, que será gerado no diretório, de dados,principal do programa.

Servidor:

* Organização: O servidor contem os dados na pasta Data.
Dentro do Data existe dois ficheiros base do servidor:
    * groups.txt: contem todos os grupos e quem pertence ao grupo, sendo o owner do grupo o primeiro utilizador a aparecer
    * passwd.txt: contem o utilizador e a sua password <username>:<password>
Cada grupo contem a sua pasta dentro do Data (Data/<group>) que contem 2 ficheiros base + photos:
    * messages_info.txt: todas as mensages (photos ou texto) enviadas no grupo, contendo o ID, se eh p (photo) ou t (texto) e quem falta ler
    * text_messages.txt: todas as mensagens de texto enviadas no grupo, o seu ID, quem enviou e o conteúdo
    * Irá ter todas as photos que forem enviadas para o grupo e que nao tenham sido vistas por todos os utilizadores do grupo

Com esta organização, o load do sistema e as operacoes de escrita em disco sao mais eficientes mas a memória ocupada é maior por haver conteúdo repetido.

* Não é possível eliminar grupos.
