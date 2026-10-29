# EntreLaços

Aplicativo de encontros executado no console, desenvolvido em Java orientado a objetos.

## Equipe

- Integrante: Henrique dos Santos Garcia
- RA: 1302221190
- Turma: Programação de soluções computacionais - Educação a Distância (EAD) | Graduação | EADESPDG-EAC-88198495

## Requisitos

- Java 27, compatível com Java 17 ou superior.
- Não há banco de dados, login ou dependências externas.
- Os dados são mantidos em memória durante a execução.
- As alterações aceitas também são registradas em `dados.json`, criado na raiz do projeto.
- Ao iniciar, os usuários, curtidas, matches e mensagens salvos no JSON são carregados novamente.

## Como executar

Na raiz do projeto, execute:

```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin App
```

No PowerShell:

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem src -Recurse -Filter *.java).FullName
java -cp bin App
```

## Principais classes

- `App`: ponto de entrada da aplicação.
- `Menu`: interação com o usuário e navegação do sistema.
- `Usuario`: dados do perfil, interesses e curtidas enviadas.
- `UsuarioService`: cadastro, validações, listagem e pesquisas.
- `Plano`, `PlanoEssencial` e `PlanoConexao`: regras polimórficas dos planos.
- `Match` e `MatchService`: formação e consulta de matches.
- `Mensagem` e `MensagemService`: envio e consulta de mensagens.
- `PersistenciaJsonService`: gravação e carregamento dos dados em JSON.
- `CatalogoInteresses`: catálogo compartilhado de interesses.
- `ExcecaoNegocio`: tratamento das regras de negócio recusadas.

## Funcionalidades

O sistema permite cadastrar e selecionar usuários, listar e pesquisar perfis, enviar curtidas, formar matches e trocar mensagens entre usuários que possuem match. O ID é gerado automaticamente e utilizado internamente; as consultas são feitas por seleções numéricas.

## Roteiro de 5 testes manuais

Execute os testes a partir da raiz do projeto com `java -cp bin App`. Após cada operação, pressione Enter quando solicitado.

### Teste 1 - Cadastro e validações

Passos:

1. Escolha `1 - Cadastrar usuário`.
2. Deixe o nome vazio e pressione Enter; confirme que o campo é solicitado novamente.
3. Informe um nome válido e deixe o e-mail vazio; confirme a repetição do campo.
4. Informe um e-mail inválido, como `usuario@dominio`; confirme a repetição.
5. Informe `usuario@dominio.com`.
6. Digite idade `17`; confirme a repetição e informe `25`.
7. Informe uma cidade com números, como `123`; confirme a repetição e informe `São Paulo`.
8. Informe `3` no plano; confirme a repetição e escolha `1` ou `2`.
9. Escolha até três interesses, tentando repetir um deles.

Resultado esperado: cada dado incorreto é corrigido no próprio campo, o cadastro é concluído e o usuário é incluído sem solicitar ID manualmente.

### Teste 2 - Seleção, listagem e pesquisa

Passos:

1. Cadastre pelo menos três usuários com nomes diferentes.
2. Escolha `2 - Selecionar usuário`.
3. Confirme que os usuários aparecem numerados por nome e cidade.
4. Escolha um número válido.
5. Escolha `3 - Listar perfis`.
6. Confirme que o usuário selecionado não aparece e que os demais estão em ordem alfabética.
7. Escolha `4 - Pesquisar perfil` e pesquise por nome.
8. Repita a pesquisa usando nome e cidade.
9. Faça uma pesquisa sem resultado.

Resultado esperado: as seleções funcionam por número, a listagem é alfabética e pesquisas sem correspondência exibem uma mensagem adequada.

### Teste 3 - Curtidas, limites e matches

Passos:

1. Selecione um usuário do plano Essencial.
2. Escolha `5 - Curtir perfil` e confirme que os perfis disponíveis aparecem numerados.
3. Escolha um perfil válido e envie a curtida.
4. Tente curtir o próprio usuário e depois repetir a mesma curtida.
5. Envie curtidas até atingir duas curtidas aceitas no plano Essencial.
6. Tente enviar uma terceira curtida.
7. Repita o fluxo com um usuário do plano Conexão, verificando o limite de cinco curtidas.
8. Faça dois usuários se curtirem mutuamente.
9. Escolha `6 - Consultar matches` para confirmar o match.

Resultado esperado: curtidas inválidas são recusadas, os limites são respeitados, tentativas recusadas não consomem limite e o match é criado uma única vez.

### Teste 4 - Mensagens e conversas

Passos:

1. Selecione um usuário sem match e escolha `7 - Enviar mensagem`.
2. Confirme que nenhuma conversa está disponível.
3. Use dois usuários com match e escolha `7 - Enviar mensagem`.
4. Selecione o contato pelo número apresentado.
5. Envie uma mensagem válida.
6. Envie uma segunda mensagem.
7. Escolha `8 - Ver conversa` e selecione o contato pelo número.
8. Tente informar uma opção de contato inválida.
9. Tente enviar uma mensagem vazia em uma conversa válida.

Resultado esperado: mensagens só são permitidas entre matches, aparecem na ordem de envio, conversas vazias são informadas e opções inválidas não solicitam texto.

### Teste 5 - Persistência e funcionamento do menu

Passos:

1. Cadastre usuários, interesses, curtidas, match e mensagens.
2. Encerre o programa com a opção `0`.
3. Abra `dados.json` e confirme os registros salvos.
4. Inicie novamente o programa.
5. Selecione um usuário salvo e consulte seus matches e conversas.
6. Cadastre um novo usuário e confirme a geração do próximo ID interno.
7. Informe uma opção inválida do menu.
8. Informe letras quando o sistema solicitar um número.
9. Escolha uma opção válida depois do erro.

Resultado esperado: os dados são recuperados do JSON, o novo ID mantém a sequência, entradas inválidas não encerram o programa e o menu continua funcionando até a opção de saída.
