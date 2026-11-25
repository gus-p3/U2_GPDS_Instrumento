FROM jenkins/jenkins:lts-jdk21
USER root
RUN apt-get update && apt-get install -y --no-install-recommends \
    maven curl git python3 python3-pip mariadb-client \
 && pip3 install --no-cache-dir --break-system-packages semgrep \
 && curl -fsSL https://railway.com/install.sh | bash -s -- -y \
 && (cp /root/.railway/bin/railway /usr/local/bin/railway || cp /root/.local/bin/railway /usr/local/bin/railway) \
 && rm -rf /var/lib/apt/lists/*