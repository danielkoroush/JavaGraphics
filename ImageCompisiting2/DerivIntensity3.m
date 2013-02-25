% Jingbin Wang, Feb 1, 2002
% *** My first work using MatLab  ****
%
% Project 1: image mosaic  
%

function [didx,didy]=DerivIntensity2(base_image,unregistered,i,j,matrix)
%
% calculate the dI/dx and dI/dy value using bilinear method
%
% i, j: the index of base_image matrix, i <--> y coordinate, j <--> x coordinate
%

base_dims=size(base_image);
i1=0;i2=0;i3=0;i4=0;

ypercent=i-floor(i);
xpercent=j-floor(j);
i=floor(i);
j=floor(j);
n=i;m=j;
if (0<m)& (m<base_dims(2)) & (0<n) &(n<base_dims(1)) & (base_image(n,m,1)~=-1)
   i1=0.3*base_image(n,m,1)+0.59*base_image(n,m,2)+0.11*base_image(n,m,3);
end

n=i+1;m=j;
if (0<m)& (m<base_dims(2)) & (0<n) &(n<base_dims(1)) & (base_image(n,m,1)~=-1)
   i2=0.3*base_image(n,m,1)+0.59*base_image(n,m,2)+0.11*base_image(n,m,3);    
end

n=i;m=j+1;
if (0<m)& (m<base_dims(2)) & (0<n) &(n<base_dims(1)) & (base_image(n,m,1)~=-1)
   i3=0.3*base_image(n,m,1)+0.59*base_image(n,m,2)+0.11*base_image(n,m,3);    
end

n=i+1;m=j+1;
if (0<m)& (m<base_dims(2)) & (0<n) &(n<base_dims(1)) & (base_image(n,m,1)~=-1)
   i4=0.3*base_image(n,m,1)+0.59*base_image(n,m,2)+0.11*base_image(n,m,3);    
end

didx=0;
if(i3~=0) & (i1~=0)
    didx=didx+(i3-i1)*(1-ypercent);
end
if(i4~=0) & (i2~=0)
    didx=didx+(i4-i2)*ypercent;
end

didy=0;
if(i1~=0) & (i2~=0)
    didy=didy+(i2-i1)*(1-xpercent);
end
if(i3~=0) & (i4~=0)
    didy=didy+(i4-i3)*xpercent;
end