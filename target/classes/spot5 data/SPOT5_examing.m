% u=zeros(20,7);
% filename={'54.spot','29.spot','42.spot','28.spot','5.spot','404.spot','408.spot','412.spot','414.spot','503.spot','505.spot','507.spot','509.spot','1401.spot','1403.spot','1405.spot','1407.spot','1502.spot','1504.spot','1506.spot'};
% P_spot5=[70;12032;108067;56053;114;49;3082;16101;22118;9096;13099;15135;19125;176056;176140;174171;176237;61158;126235;168236];
% ni_spot5=[45;34;80;47;98;33;63;81;100;72;86;94;96;148;220;250;318;166;283;306];
% v=zeros(20,1);
% for q=1:20
% S=dlmread(filename{q});
% v(q)=S(end,1);
% S=S';
% number=S(1,1);
% attribute=S(1:9,2:number+1);
% profit=attribute(2,:);                                 %ȡprofite
% M=sum(profit);
% domain=attribute(3,:);                                  %ȡdomain
% type=attribute(4:2:8,:);                                %�۲��ļ����ֵ�4�пɴ�������ķ��࣬��Ȼ��ֻ�Ǳ�ʾ�������ѡ�õĵ�һ�����
% memory=attribute(5:2:9,:);                              %�������ڲ�ͬ�����ռ�õ��ڴ�ȡ����
% identity=(1:number);
% ml=200;                 %����42�������ǵĴ洢����Ϊinf
% w=[0.5 0.5];            %Ŀ�꺯���е�Ȩ��                            
% maxgen=2;             %��������
% sizepop=20;             %��Ⱥ��ģ
% Lind=number;            %ÿ��Ⱦɫ��ĳ���Ϊnumber
% Pc=0.9;                 %�������
% Pm=0.05;                %�������
% GGAP=0.9;               %����Ϊ0.9
% probability=0.5*ones(1,number);      %��ÿ��������Ⱦɫ���еĳ��ָ���һ���ռ䣬��һ�׶� ȫΪ0-1��������ʼ���ʾ�Ϊ0.5
% type=type';
% ID=ismember(type,[1 2 3],'rows');                %�����������������ԣ��������Ϊ
% type1=identity(ID);                 %type1(0��1��2��3)   
% ID=ismember(type,[2 0 0],'rows');
% type2=identity(ID);                 %type2��0��2��
% ID=ismember(type,[3 0 0],'rows');
% type3=identity(ID);                 %type3��0��3)
% ID=ismember(type,[13 0 0],'rows');
% type4=identity(ID);                 %type4��0��13)
% u(q,1:4)=[size(type1,2),size(type2,2),size(type3,2),size(type4,2)];
% u(q,5)=sum(u(q,1:4));
% u(q,6)=number;
% u(q,7)=u(q,6)-u(q,5);
% end
% u;
% v
% 
% % 
% % file_name=['1401.spot','1403.spot','1405.spot','1407.spot','1502.spot','1504.spot','1506.spot']
% % for q=1:7
% %     filename=file_name(q);
% %end
oldchrom=randi(3,11);
Pc=0.9;
[Nind,Lind]=size(oldchrom);
Xops = floor(Nind/2);
DoCross = rand(Xops,1) < Pc;
Docross=repmat(DoCross,1,Lind);
odd = (1:2:Nind-1)';
even = (2:2:Nind)';
w_cross_odd=logical(round(rand(Xops,Lind)).*Docross);
w_cross_even=logical((1-w_cross_odd).*Docross);
selch=oldchrom;
selch_odd=oldchrom(odd,:);
selch_even=oldchrom(even,:);
selch_odd(w_cross_even)=selch_even(w_cross_even);
selch_even(w_cross_odd)=selch_odd(w_cross_odd);
selch(odd,:)=selch_odd;
selch(even,:)=selch_even;