smart-energy-esg-iot ⚡🏠🌱

API RESTful + Dashboard estático para monitorar energia & ocupação (ESG/IoT).
Stack: Spring Boot 3 (Java 21) + MongoDB Atlas + HTML/JS (Chart.js).
Inclui CRUD de devices, ingestão de telemetria e painel web simples.

🎯 Objetivo ESG: reduzir consumo com base em presença e registrar telemetria para eficiência e auditoria.

✨ Features

📡 POST /api/telemetry – recebe leituras (presence, lightOn, temperature, ts?)

🔎 GET /api/readings/latest?room=... – última leitura por sala (200 | 204 sem conteúdo)

🧰 CRUD de devices – GET/POST/PUT/DELETE /api/devices

📈 Dashboard – GET / com gráfico + status

📜 Swagger UI – GET /swagger-ui.html

❤️ CORS liberado para DEV

🧱 Arquitetura (resumo)
ESP32/Wokwi → HTTPS (ngrok)
               ↓
           API Spring Boot → MongoDB Atlas (esg_iot_db)
               ↑
        Dashboard (index.html + JS)


Coleções

devices – cadastro dos dispositivos

readings – telemetria
Índice recomendado: { room: 1, ts: -1 } (nome: idx_room_ts_desc)

🚀 Como rodar (DEV)
1) Pré-requisitos

JDK 21

Maven (ou wrapper ./mvnw)

Conta no MongoDB Atlas

2) Configure a URI (sem commitar senha)

Opção A — variável de ambiente (recomendada)

# Windows (PowerShell)
setx MONGO_URI "mongodb+srv://USUARIO:SENHA@esg-iot-cluster-rev2.59g1ri9.mongodb.net/esg_iot_db?retryWrites=true&w=majority&appName=esg-iot-cluster-rev2"
# feche e reabra o terminal/IDE


No application.properties (já no projeto) usamos:

spring.data.mongodb.uri=${MONGO_URI:mongodb://localhost:27017/esg_iot_db}
spring.data.mongodb.database=${MONGO_DB:esg_iot_db}


Se não definir MONGO_URI, cai para localhost:27017.

3) Suba a aplicação
./mvnw spring-boot:run


Dashboard: http://localhost:8080/

Swagger: http://localhost:8080/swagger-ui.html

🔌 Endpoints principais
POST   /api/telemetry
GET    /api/readings/latest?room=Sala%20101
GET    /api/devices
POST   /api/devices
GET    /api/devices/{id}
PUT    /api/devices/{id}
DELETE /api/devices/{id}

Payload de telemetria (exemplo)
{
  "deviceId": "sala-101-meter",
  "room": "Sala 101",
  "presence": true,
  "lightOn": false,
  "temperature": 24.7,
  "ts": "2025-10-25T12:34:56Z"
}

Teste rápido (Linux/macOS – curl)
curl -X POST http://localhost:8080/api/telemetry \
  -H "Content-Type: application/json" \
  -d '{"deviceId":"sala-101-meter","room":"Sala 101","presence":true,"lightOn":true,"temperature":24.3}'

Teste rápido (Windows PowerShell – Invoke-RestMethod)
$body = @{
  deviceId    = "sala-101-meter"
  room        = "Sala 101"
  presence    = $true
  lightOn     = $false
  temperature = 23.8
} | ConvertTo-Json

irm "http://localhost:8080/api/telemetry" -Method POST -ContentType "application/json" -Body $body

🗃️ MongoDB Atlas (resumo)

Database: esg_iot_db

Collections: devices, readings

Índice em readings:

{ "room": 1, "ts": -1 }


Options: { "name": "idx_room_ts_desc" }

Usuário Atlas: defina o seu (ex.: milton)

IP Allowlist (DEV): 0.0.0.0/0 (apenas para desenvolvimento)

🖥️ Front-end estático (onde fica)
src/main/resources/static/index.html
src/main/resources/static/assets/style.css
src/main/resources/static/assets/iot.js
src/main/resources/static/assets/ui.js


O painel mostra status e gráfico; e tem botão de “Enviar Telemetria” para testes.

🌐 Expondo a API (Wokwi/IoT) com ngrok

Instale e autentique o ngrok

Com a API rodando em http://localhost:8080, execute:

ngrok http 8080


Copie a URL HTTPS exibida (ex.: https://seu-subdominio.ngrok-free.dev)

Use essa URL no ESP32/Wokwi (sem “-> http://localhost:8080”
, apenas o HTTPS).

Validação

Abra no navegador:
https://SEU_SUBDOMINIO.ngrok-free.dev/api/readings/latest?room=Sala%20101

Painel do ngrok: http://127.0.0.1:4040

🤖 Simulação ESP32 (Wokwi)

Simula o envio para POST /api/telemetry via HTTPS (ngrok).

Wokwi: ESP32 → cole o sketch e ajuste:

const char* WIFI_SSID = "Wokwi-GUEST";
const char* WIFI_PASS = "";

// SOMENTE a URL HTTPS do ngrok (sem barra no final)
const String NGROK_BASE = "https://SEU_SUBDOMINIO.ngrok-free.dev";

String DEVICE_ID = "sala-101-meter";
String ROOM      = "Sala 101";


O firmware faz um GET probe antes do primeiro POST e envia JSON a cada 5s.

🧪 Seed de dados (7 dias com picos)

Você já tem os scripts que rodam no Compass/mongosh (reset + seed 7 dias).
Uso típico (mongosh):

use esg_iot_db
// cole o script de reset/seed (índices, devices padrão e leituras a cada 10 min por 7 dias)

🛠️ Troubleshooting

Windows + curl: use PowerShell Invoke-RestMethod (irm) (o curl da PS é alias diferente).

GET /api/readings/latest retorna 204: ainda não há leitura para a sala informada.

ESP32 mostra HTTP code: -1: túnel não ativo/URL errada/sem HTTPS.

ngrok erro “endpoint already online”: feche instâncias anteriores ou use --pooling-enabled.

Auth no Atlas: se ver bad auth, confira usuário/senha/DB e IP Allowlist.

🔒 Boas práticas

Não commitar credenciais. Use MONGO_URI por ambiente.

Desabilite o “Allow Access from Anywhere” em produção; restrinja IPs.

Habilite logs e métricas (Actuator/Observabilidade) apenas em ambientes controlados.

👥 Colaboradores

🧑‍💻 Hugo Correa Farranha

🧑‍💻 Milton Ribeiro

🧑‍💻 Victor Mazzola

📜 Licença

MIT — use e adapte. PRs são bem-vindos! ✨
