import matplotlib.pyplot as plt

MESSAGE_AMOUNT=[1, 10, 100, 200, 300, 1_000, 10_000, 100_000, 1_000_000]


standardnio_stat_nano = open("StandardNioStat.txt", "r").read().split(";")
del standardnio_stat_nano[-1]
standardnio_stat = [float(nano) / 1e9 for nano in standardnio_stat_nano]

gcfreenio_stat_nano = open("GCFreeNioStat.txt", "r").read().split(";")
del gcfreenio_stat_nano[-1]
gcfreenio_stat = [float(nano) / 1e9 for nano in gcfreenio_stat_nano]

print(standardnio_stat)
print(gcfreenio_stat)

# plt.axis([0, max(MESSAGE_AMOUNT), 0, max(standardnio_stat)])
plt.scatter(MESSAGE_AMOUNT, standardnio_stat, color='orange')
plt.scatter(MESSAGE_AMOUNT, gcfreenio_stat, color='green')
plt.show()









