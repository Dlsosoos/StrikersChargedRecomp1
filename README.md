# StrikersChargedRecomp1
That's an actually Super Mario Strikers Charged recompiled project It will be tested and being an APK because its an .ZIP file
# StrikersRecomp

App Android para rodar **Super Mario Strikers Charged** (R4QE01) a partir do seu próprio .iso ou .wbfs,
baseado no decomp 100% completo do projeto [decomp.dev/yannicks](https://decomp.dev/yannicks).

## Estado atual

- Menu temático em Jetpack Compose (campo listrado, abas Jogar, Config e Cheats)
- Seleção do jogo (.iso ou .wbfs) por seletor de arquivos, com permissão persistente
- Ícone e imagem do menu com a arte do Mario (arte da Nintendo: troque por outra se for publicar)
- Ponte JNI + stub nativo prontos (`app/src/main/cpp`)
- Build automático pelo GitHub Actions
- **O motor do jogo ainda não está integrado.** Config e Cheats só guardam as escolhas.
  O que falta está em [docs/PORTING.md](docs/PORTING.md).

## Como compilar

Pelo GitHub: envie o projeto, abra **Actions > Build APK** e baixe o artefato `StrikersRecomp-debug`.

Local (precisa de Android SDK, JDK 17 e Gradle 8.9):
