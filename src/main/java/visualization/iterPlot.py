
import pandas as pd
import matplotlib.pyplot as plt

# 支持中文
plt.rcParams['font.sans-serif'] = ['SimHei']  # 用来正常显示中文标签
plt.rcParams['axes.unicode_minus'] = False  # 用来正常显示负号

def iterPlot(fileName):
    # fileName文件中无小标因此需要注明下标
    iterResult = pd.read_csv(fileName, encoding='gbk')
    columns = iterResult.columns
    plt.figure(figsize=[4, 3])
    for i in range(len(columns)-1):
        df = iterResult[columns[i]].drop_duplicates()
        plt.plot(df.index, df.values, label=columns[i], linewidth=1) # color='r', marker='o',markerfacecolor='blue', markersize=12

    plt.xlabel('Iter Number')
    plt.ylabel('目标函数值')
    plt.title('不同算法迭代图')
    plt.legend()
    plt.show()

# if __name__=='__main__':
#
#     filename = 'E:Chu.csv'
#     iterPlot(filename)
