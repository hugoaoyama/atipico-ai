# ==========================================
# Estágio 1: Build da aplicação Angular
# ==========================================
FROM node:20-alpine AS build
WORKDIR /app

# Copia os arquivos de dependências
#COPY package.json package-lock.json ./
#RUN npm ci

# DEPOIS (Mais seguro e tolerante):
#COPY package.json ./
# Se você tiver o package-lock.json, pode copiar junto com um asterisco ou usar npm install:
COPY ./package.json ./package-lock.json ./
RUN npm install --legacy-peer-deps

# Copia o restante do código-fonte
COPY . .

# 1. Declarar o argumento que virá do Docker Compose
ARG ANGULAR_API_URL

# 2. Criar a pasta de environments caso não exista e injetar a URL
RUN mkdir -p src/environments && \
    echo "export const environment = { production: true, apiUrl: '${ANGULAR_API_URL}' };" > src/environments/environment.ts && \
    echo "export const environment = { production: true, apiUrl: '${ANGULAR_API_URL}' };" > src/environments/environment.production.ts

# Executa o build de produção (gera a pasta dist)
RUN npm run build --configuration=production

# ==========================================
# Estágio 2: Servidor Nginx para hospedar os arquivos estáticos
# ==========================================
FROM nginx:alpine

# Remove a página padrão do Nginx para não conflitar com o Angular
RUN rm -rf /usr/share/nginx/html/*

# Copia os arquivos compilados do estágio anterior para a pasta padrão do Nginx
# (Substitua 'seu-projeto-angular' pelo nome exato que aparece na pasta dist do seu projeto)
COPY --from=build /app/dist/atipico-ai-frontend/browser /usr/share/nginx/html

# Copia a nossa configuração customizada do Nginx para tratar as rotas do Angular
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

# Expõe a porta 80 do Nginx
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]