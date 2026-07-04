FROM openjdk:17-jdk-slim

# Instalar dependencias
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    git \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Descargar Android SDK
RUN mkdir -p /opt/android && \
    cd /opt/android && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip commandlinetools-linux-9477386_latest.zip && \
    rm commandlinetools-linux-9477386_latest.zip

# Configurar Android SDK
ENV ANDROID_HOME=/opt/android/cmdline-tools
ENV PATH=\/bin:\

# Copiar proyecto
COPY . /workspace/

# Compilar
WORKDIR /workspace
RUN gradle clean build

# Resultado
CMD ["bash"]
