#define _WINSOCK_DEPRECATED_NO_WARNINGS
#include <winsock2.h>
#include <ws2tcpip.h>
#include <iostream>
#include <thread>
#include <vector>
#include <mutex>
#include <chrono>
#include <sstream>
#include <iomanip>

#pragma comment(lib, "ws2_32.lib")

using namespace std;

mutex coutMutex;
int successCount = 0;
int failCount = 0;

string getTimestamp() {
    auto now = chrono::system_clock::now();
    time_t timeNow = chrono::system_clock::to_time_t(now);
    tm localTm;
    localtime_s(&localTm, &timeNow);

    stringstream ss;
    ss << put_time(&localTm, "%Y-%m-%d %H:%M:%S");
    return ss.str();
}

void log(const string& msg, const string& level = "INFO") {
    lock_guard<mutex> lock(coutMutex);
    string prefix = "[" + getTimestamp() + "] [" + level + "] ";
    if (level == "ERROR") {
        cerr << prefix << msg << "\n";
    } else {
        cout << prefix << msg << "\n";
    }
}

void runClient(int id) {
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        log("Thread " + to_string(id) + ": WSAStartup failed", "ERROR");
        ++failCount;
        return;
    }

    SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCKET) {
        log("Thread " + to_string(id) + ": Socket creation failed", "ERROR");
        ++failCount;
        WSACleanup();
        return;
    }

    sockaddr_in serverAddr{};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(8000);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");

    auto start = chrono::high_resolution_clock::now();
    if (connect(sock, (sockaddr*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        log("Thread " + to_string(id) + ": Connection failed", "ERROR");
        closesocket(sock);
        ++failCount;
        WSACleanup();
        return;
    }

    const char* message = "Hello from Windows client!";
    send(sock, message, strlen(message), 0);

    char buffer[1024] = {0};
    int bytesReceived = recv(sock, buffer, sizeof(buffer) - 1, 0);
    auto end = chrono::high_resolution_clock::now();
    auto latency = chrono::duration_cast<chrono::milliseconds>(end - start).count();

    if (bytesReceived > 0) {
        lock_guard<mutex> lock(coutMutex);
        ++successCount;
        cout << "[" << getTimestamp() << "] [RESPONSE] Thread " << id
             << ": \"" << buffer << "\" (" << latency << " ms)\n";
    } else {
        log("Thread " + to_string(id) + ": No data received", "ERROR");
        ++failCount;
    }

    closesocket(sock);
    WSACleanup();
}

int main() {
    const int threadCount = 10000;
    vector<thread> threads;
    threads.reserve(threadCount);

    log("Starting test with " + to_string(threadCount) + " clients...");

    for (int i = 0; i < threadCount; ++i) {
        threads.emplace_back(runClient, i);
    }

    for (auto& t : threads) {
        if (t.joinable()) t.join();
    }

    log("Test complete", "SUCCESS");
    log("Total Success: " + to_string(successCount));
    log("Total Failures: " + to_string(failCount));
    return 0;
}