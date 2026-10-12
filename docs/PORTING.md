# Roteiro do port

O decomp reproduz o código original, mas ele foi escrito para o Wii (PowerPC, big-endian,
ponteiros de 32 bits, SDK da Nintendo). Para rodar no Android (ARM64) falta:

1. **Trazer o decomp** como submódulo em `third_party/strikers` (use o link do repositório do projeto).
2. **Compilar com clang/NDK** em vez do CodeWarrior e corrigir o que não é portátil:
   endianness dos dados lidos do disco, tamanho de ponteiros, assembly inline, extensões do compilador.
3. **Reimplementar a camada do Wii** em `app/src/main/cpp/platform/`:
   - Gráficos: comandos GX para OpenGL ES 3 ou Vulkan
   - Áudio: saída de áudio (AAudio/Oboe)
   - Entrada: Wiimote/Nunchuk para toque e controles Bluetooth
   - Disco: leitura do jogo pelo descritor de arquivo recebido em `nativeStart`. O .iso lê direto; o .wbfs é um contêiner e precisa de um leitor (ou conversão) antes de montar o sistema de arquivos do disco
   - Sistema: threads, memória, relógio e filas do OS do Wii
4. **Ligar ao CMake** (`app/src/main/cpp/CMakeLists.txt`) e chamar o jogo em `nativeStart`.
5. **Aplicar cheats e config**: `nativeStart` já recebe a máscara de cheats (um bit por item de
   `CHEATS` em `Screens.kt`) e a escala de resolução.

Referências de portes feitos assim: sm64-port e Ship of Harkinian.
