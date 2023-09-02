# Object-oriented programming - Warehouse Manager Project

## Created with ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)

## Branches
- [1st Submited Version (feat/CORE/Version1)](https://gitlab.com/goncaloasilva/po-project/-/tree/feat/CORE/Version1)
- [2nd Submited Version (fix/CORE/Version2)](https://gitlab.com/goncaloasilva/po-project/-/tree/fix/CORE/Version2)

## Authors
This project was made by:
- (96925) Gonçalo Abreu Corrêa Brito da Silva
- (99113) Miguel Ribeiro Valadão do Vale

@ Copyrights 2021 (Excluding ggc_skeleton and po-uilib)

## Useful documentation links
[Project at Fenix](https://fenix.tecnico.ulisboa.pt/disciplinas/PO2/2021-2022/1-semestre/projecto)
<br>
[Technical Project Page](https://web.tecnico.ulisboa.pt/~david.matos/w/pt/index.php/Programação_com_Objectos/Projecto_de_Programação_com_Objectos/Enunciado_do_Projecto_de_2021-2022)

## To do:
- [x] Menu principal: Mostrar data actual e Avançar data actual
- [x] [Menu de Gestão de Parceiros](https://web.tecnico.ulisboa.pt/~david.matos/w/pt/index.php/Programação_com_Objectos/Projecto_de_Programação_com_Objectos/Enunciado_do_Projecto_de_2021-2022#Menu_de_Gest.C3.A3o_de_Parceiros) -- implementar as operações "Mostrar parceiro", "Mostrar todos os parceiros" e "Registar parceiro".
- [x] [Menu de Gestão de Produtos](https://web.tecnico.ulisboa.pt/~david.matos/w/pt/index.php/Programação_com_Objectos/Projecto_de_Programação_com_Objectos/Enunciado_do_Projecto_de_2021-2022#Menu_de_Gest.C3.A3o_de_Produtos) -- implementar as operações "Visualizar todos os produtos? e "Visualizar todos os lotes""...
- [x] Menu principal: [Gestão e consulta de dados da aplicação](https://web.tecnico.ulisboa.pt/~david.matos/w/pt/index.php/Programação_com_Objectos/Projecto_de_Programação_com_Objectos/Enunciado_do_Projecto_de_2021-2022#Gest.C3.A3o_e_consulta_de_dados_da_aplica.C3.A7.C3.A3o): abertura dos vários submenus (comandos fornecidos já implementados).
- [x] Menu principal: [Salvaguarda do estado actual da aplicação](https://web.tecnico.ulisboa.pt/~david.matos/w/pt/index.php/Programação_com_Objectos/Projecto_de_Programação_com_Objectos/Enunciado_do_Projecto_de_2021-2022#Salvaguarda_do_estado_actual_da_aplica.C3.A7.C3.A3o): "Abrir" e "Guardar" -- implementação completa
- [x] Leitura, interpretação e representação em memória do ficheiro textual indicado pela propriedade [import](https://web.tecnico.ulisboa.pt/~david.matos/w/pt/index.php/Programação_com_Objectos/Projecto_de_Programação_com_Objectos/Enunciado_do_Projecto_de_2021-2022#Leitura_de_Dados_a_Partir_de_Ficheiros_Textuais) (implica implementar algumas classes do "core")

## Repository Structure Patterns

### Branch Structure
For branches use: <br>
`(type)/(layer)/(package)`
<br><br>
`(type)`
- **feat** : For features that will be implemented
- **fix**  : For fixing features that already exist

`(layer)`
- **APP**  : Application User Interaction Layer
- **CORE** : Application Core Layer
- **GENERAL** : When implementing code on APP and CORE layers

`(package)`<br>
The name of the package or module (can also be a class) that you are implementing

### Commit Structure
For commits use: <br>
`(type): (desc)`
<br><br>
`(type)`
- **feat** : For new methods (or implements, extends)
- **change** : For changing methods that already exist
- **fix**  : For fixing methods that already exist

`(desc)`<br>
Changes description

