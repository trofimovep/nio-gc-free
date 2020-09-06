import matplotlib.pyplot as plt
import numpy as np

MESSAGE_AMOUNT = [1, 10, 100, 200, 300, 1_000, 10_000, 100_000, 1_000_000, 2_000_000, 3_000_000, 3_500_000, 4_000_000, 5_000_000, 6_000_000]

standardnio_stat_nano = open("StandardNioStat.txt", "r").read().split(";")
del standardnio_stat_nano[-1]
standardnio_stat = [float(nano) / 1e9 for nano in standardnio_stat_nano]

gcfreenio_stat_nano = open("GCFreeNioStat.txt", "r").read().split(";")
del gcfreenio_stat_nano[-1]
gcfreenio_stat = [float(nano) / 1e9 for nano in gcfreenio_stat_nano]

print(standardnio_stat)
print(gcfreenio_stat)


plt.xlabel('Messages amount')
plt.ylabel('time (sec)')

plt.plot(MESSAGE_AMOUNT, standardnio_stat, 'o', color='orange', label='standard nio')
plt.plot(MESSAGE_AMOUNT, gcfreenio_stat, 'o', color='green', label='gc-free nio')

x = np.array(MESSAGE_AMOUNT)
# regression for standardnio_stat
a, b = np.polyfit(MESSAGE_AMOUNT, standardnio_stat, 1)
# regression for gcfreenio_stat
a1, b1 = np.polyfit(MESSAGE_AMOUNT, gcfreenio_stat, 1)

plt.plot(x, a * x  + b, color='orange', label='regression for standard nio')
plt.plot(x, a1 * x  + b1, color='green', label='regression for gc-free nio')

plt.legend()
plt.show()










